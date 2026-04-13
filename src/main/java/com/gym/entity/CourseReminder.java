package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_reminder")
public class CourseReminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @TableField("booking_id")
    private Long bookingId;

    @TableField("course_id")
    private Long courseId;

    @TableField("course_name")
    private String courseName;

    @TableField("coach_name")
    private String coachName;

    @TableField("class_time")
    private LocalDateTime classTime;

    @TableField("location")
    private String location;

    @TableField("remind_status")
    private Integer remindStatus;

    @TableField("remind_time")
    private LocalDateTime remindTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
