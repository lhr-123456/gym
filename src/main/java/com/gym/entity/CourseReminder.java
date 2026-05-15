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

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("yuyue_id")
    private Long bookingId;

    @TableField("kecheng_id")
    private Long courseId;

    @TableField("kecheng_ming")
    private String courseName;

    @TableField("jiaolian_ming")
    private String coachName;

    @TableField("keshi_shijian")
    private LocalDateTime classTime;

    @TableField("weizhi")
    private String location;

    @TableField("tixing_zhuangtai")
    private Integer remindStatus;

    @TableField("tixing_shijian")
    private LocalDateTime remindTime;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
