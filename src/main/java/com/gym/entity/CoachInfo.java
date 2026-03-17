package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("coach_info")
public class CoachInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "coach_id", type = IdType.AUTO)
    private Long coachId;

    @TableField("coach_name")
    private String coachName;

    @TableField("gender")
    private String gender;

    @TableField("birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthDate;

    @TableField("phone_num")
    private String phoneNum;

    @TableField("email_addr")
    private String emailAddr;

    @TableField("specialty")
    private String specialty;

    @TableField("experience_years")
    private Integer experienceYears;

    @TableField("certification")
    private String certification;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("status")
    private Integer status;

    @TableField(value = "hire_date", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate hireDate;

    @TableLogic
    private Integer deleted;
}
