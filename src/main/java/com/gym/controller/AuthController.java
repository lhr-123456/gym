package com.gym.controller;

import com.gym.config.JwtUtil;
import com.gym.dto.LoginRequest;
import com.gym.dto.LoginResponse;
import com.gym.dto.ApiResponse;
import com.gym.entity.UserInfo;
import com.gym.mapper.UserInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil, 
                         UserInfoMapper userInfoMapper,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userInfoMapper = userInfoMapper;
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

            // 生成JWT令牌
            String token = jwtUtil.generateToken(
                userInfo.getUsername(), 
                userInfo.getUserId(), 
                userInfo.getUserType()
            );

            // 创建响应对象
            String role = getUserRole(userInfo.getUserType());
            LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(userInfo.getUsername())
                .userId(userInfo.getUserId())
                .userType(userInfo.getUserType())
                .role(role)
                .build();

            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserInfo userInfo) {
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getUsername, userInfo.getUsername());
            UserInfo existingUser = userInfoMapper.selectOne(wrapper);

            if (existingUser != null) {
                return ApiResponse.error("用户名已存在");
            }

            // 加密密码
            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            userInfo.setStatus(1); // 默认启用

            // 保存用户信息
            userInfoMapper.insert(userInfo);

            return ApiResponse.success("注册成功");
        } catch (Exception e) {
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
}
