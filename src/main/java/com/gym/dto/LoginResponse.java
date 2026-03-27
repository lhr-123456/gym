package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String username;
    private Long userId;
    private Integer userType;
    private String role;
    /**
     * 会员ID（仅 userType=3 时有值）
     */
    private Long memberId;
    /**
     * 教练ID（仅 userType=2 时有值）
     */
    private Long coachId;
    /**
     * 头像路径
     */
    private String avatar;
}
