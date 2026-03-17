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

    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Long scheduleId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("schedule_date")
    private LocalDate scheduleDate;

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("schedule_type")
    private String scheduleType;

    @TableField("course_id")
    private Long courseId;

    @TableField("course_name")
    private String courseName;

    @TableField("location")
    private String location;

    @TableField("max_capacity")
    private Integer maxCapacity;

    @TableField("current_capacity")
    private Integer currentCapacity;

    @TableField("status")
    private Integer status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
