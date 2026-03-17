package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_review")
public class CoachReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("member_id")
    private Long memberId;

    @TableField("course_id")
    private Long courseId;

    @TableField("rating")
    private Integer rating;

    @TableField("teaching_rating")
    private Integer teachingRating;

    @TableField("service_rating")
    private Integer serviceRating;

    @TableField("professional_rating")
    private Integer professionalRating;

    @TableField("review_content")
    private String reviewContent;

    @TableField("review_pics")
    private String reviewPics;

    @TableField("reply_content")
    private String replyContent;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
