package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_review")
public class CourseReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pingjia_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("kecheng_id")
    private Long courseId;

    @TableField(exist = false)
    private String courseName;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField(exist = false)
    private String memberName;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableField("pingfen")
    private Integer rating;

    @TableField("neirong")
    private String content;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("huifu_neirong")
    private String reply;

    @TableField("huifu_shijian")
    private LocalDateTime replyTime;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
