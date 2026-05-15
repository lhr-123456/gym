package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("coach_schedule")
public class CoachSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "paiban_id", type = IdType.AUTO)
    private Long scheduleId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("paiban_riqi")
    private LocalDate scheduleDate;

    @TableField("kaishi_shijian")
    private LocalTime startTime;

    @TableField("jieshu_shijian")
    private LocalTime endTime;

    @TableField("paiban_leixing")
    private String scheduleType;

    @TableField("kecheng_id")
    private Long courseId;

    @TableField("kecheng_ming")
    private String courseName;

    @TableField("weizhi")
    private String location;

    @TableField("zuida_renshu")
    private Integer maxCapacity;

    @TableField("dangqian_renshu")
    private Integer currentCapacity;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
