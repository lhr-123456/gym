package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_signin")
public class MemberSignin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "signin_id", type = IdType.AUTO)
    private Long signinId;

    @TableField("member_id")
    private Long memberId;

    @TableField("signin_date")
    private LocalDate signinDate;

    @TableField("signin_time")
    private LocalDateTime signinTime;

    @TableField("signin_type")
    private String signinType;

    @TableField("points_earned")
    private Integer pointsEarned;

    @TableField("consecutive_days")
    private Integer consecutiveDays;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
