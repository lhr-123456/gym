package com.gym.controller;

import com.gym.config.JwtUtil;
import com.gym.dto.LoginRequest;
import com.gym.dto.LoginResponse;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberInfo;
import com.gym.entity.UserInfo;
import com.gym.mapper.MemberInfoMapper;
import com.gym.mapper.UserInfoMapper;
import com.gym.mapper.CoachInfoMapper;
import com.gym.entity.CoachInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserInfoMapper userInfoMapper;
    private final MemberInfoMapper memberInfoMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtUtil jwtUtil,
                         UserInfoMapper userInfoMapper,
                         MemberInfoMapper memberInfoMapper,
                         CoachInfoMapper coachInfoMapper,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userInfoMapper = userInfoMapper;
        this.memberInfoMapper = memberInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 使用用户名和密码进行身份验证，并将认证信息设置到 SecurityContext
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, loginRequest.getUsername());
            UserInfo userInfo = userInfoMapper.selectOne(wrapper);

            if (userInfo == null || userInfo.getStatus() != 1) {
                return ApiResponse.error("用户不存在或已被禁用");
            }

            // 校验用户类型（前端登录页会选择用户类型；若与数据库不一致，直接拒绝，避免登录到错误端导致“页面不符合预期”）
            if (loginRequest.getUserType() != null
                && userInfo.getUserType() != null
                && !loginRequest.getUserType().equals(userInfo.getUserType())) {
                return ApiResponse.error("用户类型不匹配，请选择正确的用户类型登录");
            }

            // 生成JWT令牌（携带 memberId/coachId 方便各 Controller 直接从 request 取用）
            String token = jwtUtil.generateToken(
                userInfo.getUsername(),
                userInfo.getUserId(),
                userInfo.getUserType(),
                userInfo.getMemberId(),
                userInfo.getCoachId()
            );

            // 查询头像：会员查 member_info.avatar，教练查 coach_info.avatar_url
            String avatar = null;
            if (userInfo.getUserType() != null && userInfo.getUserType() == 3) {
                Long memberId = userInfo.getMemberId();
                if (memberId == null) {
                    // 兜底：按用户名查会员档案
                    LambdaQueryWrapper<MemberInfo> mb = new LambdaQueryWrapper<>();
                    mb.eq(MemberInfo::getMemberName, userInfo.getUsername());
                    MemberInfo fallback = memberInfoMapper.selectOne(mb);
                    if (fallback != null) memberId = fallback.getMemberId();
                }
                if (memberId != null) {
                    MemberInfo member = memberInfoMapper.selectById(memberId);
                    if (member != null) avatar = member.getAvatar();
                }
            } else if (userInfo.getUserType() != null && userInfo.getUserType() == 2 && userInfo.getCoachId() != null) {
                CoachInfo coach = coachInfoMapper.selectById(userInfo.getCoachId());
                if (coach != null) avatar = coach.getAvatarUrl();
            }

            // 创建响应对象
            String role = getUserRole(userInfo.getUserType());
            LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(userInfo.getUsername())
                .userId(userInfo.getUserId())
                .userType(userInfo.getUserType())
                .role(role)
                .memberId(userInfo.getMemberId())
                .coachId(userInfo.getCoachId())
                .avatar(avatar)
                .build();

            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserInfo userInfo) {
        try {
            System.out.println("========== 注册请求开始 ==========");
            System.out.println("接收到的注册信息: username=" + userInfo.getUsername() + ", userType=" + userInfo.getUserType());

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, userInfo.getUsername());
            UserInfo existingUser = userInfoMapper.selectOne(wrapper);

            if (existingUser != null) {
                return ApiResponse.error("用户名已存在");
            }

            // 保存原始密码用于日志
            String rawPassword = userInfo.getPassword();
            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            userInfo.setStatus(1);
            int userInsertResult = userInfoMapper.insert(userInfo);
            System.out.println("UserInfo 插入结果: " + userInsertResult + ", userId=" + userInfo.getUserId());

            // 如果注册的是会员类型，自动创建 MemberInfo 档案
            Integer ut = userInfo.getUserType();
            System.out.println("用户类型: " + ut);
            if (ut != null && ut == 3) {
                MemberInfo member = new MemberInfo();
                member.setMemberName(userInfo.getUsername());
                member.setPhoneNum("MEMBER_" + System.currentTimeMillis());
                member.setFitnessLevel("初级");
                member.setMemberLevel(1);
                member.setPoints(0);
                member.setBalance(0.0);
                member.setAccountStatus(0);
                member.setRegTime(LocalDateTime.now());
                int memberInsertResult = memberInfoMapper.insert(member);
                System.out.println("MemberInfo 插入结果: " + memberInsertResult + ", memberId=" + member.getMemberId());

                // 回填 user_info.member_id 关联
                userInfo.setMemberId(member.getMemberId());
                int updateResult = userInfoMapper.updateById(userInfo);
                System.out.println("UserInfo 回填 memberId 结果: " + updateResult);
            }

            // 如果注册的是教练类型，自动创建 CoachInfo 档案
            if (ut != null && ut == 2) {
                CoachInfo coach = new CoachInfo();
                coach.setCoachName(userInfo.getUsername());
                coach.setGender("");
                coach.setPhoneNum("COACH_" + System.currentTimeMillis());
                coach.setEmailAddr("");
                coach.setSpecialty("");
                coach.setExperienceYears(0);
                coach.setCertification("");
                coach.setStatus(1);
                int coachInsertResult = coachInfoMapper.insert(coach);
                System.out.println("CoachInfo 插入结果: " + coachInsertResult + ", coachId=" + coach.getCoachId());

                // 回填 user_info.coach_id 关联
                userInfo.setCoachId(coach.getCoachId());
                int updateResult = userInfoMapper.updateById(userInfo);
                System.out.println("UserInfo 回填 coachId 结果: " + updateResult);
            }

            System.out.println("========== 注册请求完成 ==========");
            return ApiResponse.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("注册异常: " + e.getMessage());
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }

    private String getUserRole(Integer userType) {
        if (userType == null) {
            return "MEMBER";
        }
        switch (userType) {
            case 1:
                return "ADMIN";
            case 2:
                return "COACH";
            case 3:
            default:
                return "MEMBER";
        }
    }

    @GetMapping("/profile")
    public ApiResponse<UserInfo> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, username);
            UserInfo userInfo = userInfoMapper.selectOne(wrapper);

            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败：" + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(@RequestBody UserInfo userInfo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, username);
            UserInfo existingUser = userInfoMapper.selectOne(wrapper);

            if (existingUser == null) {
                return ApiResponse.error("用户不存在");
            }

            // 更新用户信息（除了密码和用户类型）
            if (userInfo.getMemberId() != null) {
                existingUser.setMemberId(userInfo.getMemberId());
            }
            if (userInfo.getCoachId() != null) {
                existingUser.setCoachId(userInfo.getCoachId());
            }

            userInfoMapper.updateById(existingUser);

            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新用户信息失败：" + e.getMessage());
        }
    }

    @PutMapping("/password")
    public ApiResponse<String> updatePassword(@RequestBody Map<String, String> passwordRequest) {
        try {
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ApiResponse.error("密码不能为空");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, username);
            UserInfo userInfo = userInfoMapper.selectOne(wrapper);

            if (userInfo == null) {
                return ApiResponse.error("用户不存在");
            }

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, userInfo.getPassword())) {
                return ApiResponse.error("原密码错误");
            }

            // 更新密码
            userInfo.setPassword(passwordEncoder.encode(newPassword));
            userInfoMapper.updateById(userInfo);

            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            return ApiResponse.error("修改密码失败：" + e.getMessage());
        }
    }

    // 临时接口：修复用户类型（仅用于调试）
    @PutMapping("/fix-user-type")
    public ApiResponse<String> fixUserType(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            Integer userType = (Integer) request.get("userType");
            Long coachId = request.get("coachId") != null ? Long.valueOf(request.get("coachId").toString()) : null;

            if (username == null || userType == null) {
                return ApiResponse.error("参数不完整");
            }

            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, username);
            UserInfo userInfo = userInfoMapper.selectOne(wrapper);

            if (userInfo == null) {
                return ApiResponse.error("用户不存在");
            }

            userInfo.setUserType(userType);
            if (coachId != null) {
                userInfo.setCoachId(coachId);
            }
            userInfoMapper.updateById(userInfo);

            return ApiResponse.success("用户类型已修复为: " + userType);
        } catch (Exception e) {
            return ApiResponse.error("修复失败：" + e.getMessage());
        }
    }
}
