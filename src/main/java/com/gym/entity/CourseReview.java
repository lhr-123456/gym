package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_review")
public class CourseReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("course_id")
    private Long courseId;

    @TableField(exist = false)
    private String courseName;

    @TableField("member_id")
    private Long memberId;

    @TableField(exist = false)
    private String memberName;

    @TableField("coach_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableField("rating")
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField("status")
    private Integer status;

    @TableField("reply")
    private String reply;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
