package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.config.JwtUtil;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberInfo;
import com.gym.entity.UserInfo;
import com.gym.mapper.MemberInfoMapper;
import com.gym.mapper.UserInfoMapper;
import com.gym.service.MemberInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberInfoController {

    private final MemberInfoService memberInfoService;
    private final JwtUtil jwtUtil;
    private final UserInfoMapper userInfoMapper;
    private final MemberInfoMapper memberInfoMapper;

    /** 会员用户类型，与 AuthController 中一致 */
    private static final int USER_TYPE_MEMBER = 3;

    public MemberInfoController(MemberInfoService memberInfoService, JwtUtil jwtUtil,
                               UserInfoMapper userInfoMapper, MemberInfoMapper memberInfoMapper) {
        this.memberInfoService = memberInfoService;
        this.jwtUtil = jwtUtil;
        this.userInfoMapper = userInfoMapper;
        this.memberInfoMapper = memberInfoMapper;
    }

    @GetMapping("/page")
    public ApiResponse<Page<MemberInfo>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            MemberInfo memberInfo) {
        Page<MemberInfo> page = memberInfoService.getMemberPage(pageNum, pageSize, memberInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<MemberInfo>> getList(MemberInfo memberInfo) {
        List<MemberInfo> list = memberInfoService.list(memberInfo);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberInfo> getById(@PathVariable Long id) {
        MemberInfo memberInfo = memberInfoService.getById(id);
        if (memberInfo == null) {
            return ApiResponse.error("会员不存在");
        }
        return ApiResponse.success(memberInfo);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody MemberInfo memberInfo) {
        boolean result = memberInfoService.save(memberInfo);
        if (result) {
            return ApiResponse.success("添加会员成功");
        }
        return ApiResponse.error("添加会员失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberInfo memberInfo) {
        if (memberInfo.getMemberId() == null) {
            return ApiResponse.error("会员 ID 不能为空");
        }
        boolean result = memberInfoService.updateById(memberInfo);
        if (result) {
            return ApiResponse.success("更新会员成功");
        }
        return ApiResponse.error("更新会员失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = memberInfoService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除会员成功");
        }
        return ApiResponse.error("删除会员失败");
    }

    /**
     * 分配教练给会员
     */
    @PutMapping("/assign-coach")
    public ApiResponse<String> assignCoach(@RequestBody MemberInfo memberInfo) {
        if (memberInfo.getMemberId() == null) {
            return ApiResponse.error("会员ID不能为空");
        }
        MemberInfo existing = memberInfoService.getById(memberInfo.getMemberId());
        if (existing == null) {
            return ApiResponse.error("会员不存在");
        }
        existing.setCoachId(memberInfo.getCoachId());
        boolean result = memberInfoService.updateById(existing);
        if (result) {
            return ApiResponse.success("教练分配成功");
        }
        return ApiResponse.error("教练分配失败");
    }

    @GetMapping("/points/{id}")
    public ApiResponse<Integer> getPoints(@PathVariable Long id) {
        MemberInfo memberInfo = memberInfoService.getById(id);
        if (memberInfo == null) {
            return ApiResponse.error("会员不存在");
        }
        return ApiResponse.success(memberInfo.getPoints() != null ? memberInfo.getPoints() : 0);
    }

    /**
     * 调整会员积分
     */
    @PutMapping("/points/{id}")
    public ApiResponse<String> adjustPoints(@PathVariable Long id, @RequestBody PointsAdjustDTO dto) {
        MemberInfo memberInfo = memberInfoService.getById(id);
        if (memberInfo == null) {
            return ApiResponse.error("会员不存在");
        }
        
        int currentPoints = memberInfo.getPoints() != null ? memberInfo.getPoints() : 0;
        int newPoints = currentPoints + dto.getPointsChange();
        
        // 确保积分不会为负数
        if (newPoints < 0) {
            return ApiResponse.error("积分调整后不能为负数");
        }
        
        memberInfo.setPoints(newPoints);
        boolean result = memberInfoService.updateById(memberInfo);
        if (result) {
            return ApiResponse.success("积分调整成功，当前积分：" + newPoints);
        }
        return ApiResponse.error("积分调整失败");
    }

    /**
     * 会员上传头像，从 JWT 中取当前用户 ID，自动关联会员
     */
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("请选择要上传的图片");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
            (!originalFilename.toLowerCase().endsWith(".jpg") &&
             !originalFilename.toLowerCase().endsWith(".jpeg") &&
             !originalFilename.toLowerCase().endsWith(".png") &&
             !originalFilename.toLowerCase().endsWith(".gif") &&
             !originalFilename.toLowerCase().endsWith(".webp"))) {
            return ApiResponse.error("仅支持 jpg、png、gif、webp 格式的图片");
        }
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            UserInfo userInfo = userInfoMapper.selectById(userId);
            if (userInfo == null) {
                return ApiResponse.error("用户不存在");
            }
            MemberInfo memberInfo = resolveMemberForLoginUser(userInfo);
            if (memberInfo == null) {
                return ApiResponse.error("会员信息不存在，请联系管理员绑定会员档案");
            }

            // 保存文件到 uploads/avatar/
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatar" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String ext = "";
            int dotIdx = originalFilename.lastIndexOf('.');
            if (dotIdx > 0) {
                ext = originalFilename.substring(dotIdx);
            }
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(uploadDir + newFileName);
            file.transferTo(dest);

            String avatarPath = "uploads/avatar/" + newFileName;
            // 同时更新会员记录的头像字段
            memberInfo.setAvatar(avatarPath);
            memberInfoService.updateById(memberInfo);

            return ApiResponse.success(avatarPath);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 解析当前登录用户对应的会员档案。
     * user_info.member_id 可能未维护：依次尝试 member_id、按会员姓名匹配用户名、member_id=user_id。
     */
    private MemberInfo resolveMemberForLoginUser(UserInfo userInfo) {
        Long mid = userInfo.getMemberId();
        if (mid != null) {
            MemberInfo m = memberInfoService.getById(mid);
            if (m != null) {
                return m;
            }
        }
        if (userInfo.getUserType() != null && userInfo.getUserType() == USER_TYPE_MEMBER) {
            LambdaQueryWrapper<MemberInfo> byName = new LambdaQueryWrapper<>();
            byName.eq(MemberInfo::getMemberName, userInfo.getUsername());
            MemberInfo byUsername = memberInfoMapper.selectOne(byName);
            if (byUsername != null) {
                userInfo.setMemberId(byUsername.getMemberId());
                userInfoMapper.updateById(userInfo);
                return byUsername;
            }
        }
        // 部分数据初始化时 member_id 与 user_id 相同
        MemberInfo sameId = memberInfoService.getById(userInfo.getUserId());
        if (sameId != null) {
            userInfo.setMemberId(sameId.getMemberId());
            userInfoMapper.updateById(userInfo);
            return sameId;
        }
        return null;
    }
}

/**
 * 积分调整DTO
 */
class PointsAdjustDTO {
    private int pointsChange; // 积分变化量，正数为增加，负数为减少
    private String reason; // 调整原因
    
    public int getPointsChange() {
        return pointsChange;
    }
    
    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
