package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("coach_shift_change")
public class CoachShiftChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "huanban_id", type = IdType.AUTO)
    private Long shiftId;

    @TableField("yuan_paiban_id")
    private Long originalScheduleId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("yuan_riqi")
    private LocalDate originalDate;

    @TableField("yuan_kaishi")
    private LocalTime originalStartTime;

    @TableField("yuan_jieshu")
    private LocalTime originalEndTime;

    @TableField("mubiao_riqi")
    private LocalDate targetDate;

    @TableField("mubiao_kaishi")
    private LocalTime targetStartTime;

    @TableField("mubiao_jieshu")
    private LocalTime targetEndTime;

    @TableField("yuanyin")
    private String reason;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("pizhun_ren")
    private Long approveBy;

    @TableField("pizhun_shijian")
    private LocalDateTime approveTime;

    @TableField("jujue_yuanyin")
    private String rejectReason;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
