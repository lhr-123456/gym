package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_specialty")
public class CoachSpecialty implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "specialty_id", type = IdType.AUTO)
    private Long specialtyId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("specialty_name")
    private String specialtyName;

    @TableField("specialty_desc")
    private String specialtyDesc;

    @TableField("level")
    private Integer level;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
