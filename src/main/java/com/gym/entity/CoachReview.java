package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_review")
public class CoachReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pingjia_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("kecheng_id")
    private Long courseId;

    @TableField("zonghe_pingfen")
    private Integer rating;

    @TableField("jiaoxue_pingfen")
    private Integer teachingRating;

    @TableField("fuwu_pingfen")
    private Integer serviceRating;

    @TableField("zhuanye_pingfen")
    private Integer professionalRating;

    @TableField("pingjia_neirong")
    private String reviewContent;

    @TableField("pingjia_tupian")
    private String reviewPics;

    @TableField("huifu_neirong")
    private String replyContent;

    @TableField("huifu_shijian")
    private LocalDateTime replyTime;

    @TableField("zhuangtai")
    private Integer status;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
