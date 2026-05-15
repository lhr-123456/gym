package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_specialty")
public class CoachSpecialty implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "zhuanye_id", type = IdType.AUTO)
    private Long specialtyId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("zhuanye_ming")
    private String specialtyName;

    @TableField("zhuanye_miaoshu")
    private String specialtyDesc;

    @TableField("dengji")
    private Integer level;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
