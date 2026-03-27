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

    @TableId(value = "shift_id", type = IdType.AUTO)
    private Long shiftId;

    @TableField("original_schedule_id")
    private Long originalScheduleId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("original_date")
    private LocalDate originalDate;

    @TableField("original_start_time")
    private LocalTime originalStartTime;

    @TableField("original_end_time")
    private LocalTime originalEndTime;

    @TableField("target_date")
    private LocalDate targetDate;

    @TableField("target_start_time")
    private LocalTime targetStartTime;

    @TableField("target_end_time")
    private LocalTime targetEndTime;

    @TableField("reason")
    private String reason;

    @TableField("status")
    private Integer status;

    @TableField("approve_by")
    private Long approveBy;

    @TableField("approve_time")
    private LocalDateTime approveTime;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
