package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_homework")
public class CoachHomework implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("coach_id")
    private Long coachId;

    @TableField("member_id")
    private Long memberId;

    @TableField("course_booking_id")
    private Long courseBookingId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("target_date")
    private LocalDate targetDate;

    @TableField("status")
    private Integer status;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField("coach_remark")
    private String coachRemark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
