package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_info")
public class MemberInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "member_id", type = IdType.AUTO)
    private Long memberId;

    @TableField("member_name")
    private String memberName;

    @TableField("gender")
    private String gender;

    @TableField("birth_date")
    private LocalDate birthDate;

    @TableField("phone_num")
    private String phoneNum;

    @TableField("email_addr")
    private String emailAddr;

    @TableField(value = "reg_time", fill = FieldFill.INSERT)
    private LocalDateTime regTime;

    @TableField("fitness_level")
    private String fitnessLevel;

    @TableField("last_visit")
    private LocalDateTime lastVisit;

    @TableField("account_status")
    private Integer accountStatus;

    @TableField("member_level")
    private Integer memberLevel;

    @TableField("points")
    private Integer points;

    @TableField("balance")
    private Double balance;

    @TableField("avatar")
    private String avatar;

    @TableField("coach_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableLogic
    private Integer deleted;
}
