package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_booking")
public class CourseBooking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "booking_id", type = IdType.AUTO)
    private Long bookingId;

    @TableField("member_id")
    private Long memberId;

    @TableField("course_id")
    private Long courseId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("booking_time")
    private LocalDateTime bookingTime;

    @TableField("class_time")
    private LocalDateTime classTime;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
