package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course_booking")
public class CourseBooking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "yuyue_id", type = IdType.AUTO)
    private Long bookingId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField(exist = false)
    private String memberName;

    @TableField("kecheng_id")
    private Long courseId;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String courseType;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableField("yuyue_shijian")
    private LocalDateTime bookingTime;

    @TableField("keshi_shijian")
    private LocalDateTime classTime;

    @TableField("zhuangtai")
    private String status;

    @TableField("beizhu")
    private String remark;

    @TableField("qiandao_shijian")
    private LocalDateTime signinTime;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
