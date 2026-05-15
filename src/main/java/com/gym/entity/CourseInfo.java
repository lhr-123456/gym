package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("course_info")
public class CourseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "kecheng_id", type = IdType.AUTO)
    private Long courseId;

    @TableField("kecheng_ming")
    private String courseName;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableField("kecheng_leixing")
    private String courseType;

    @TableField("fenlei_id")
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;

    @TableField("miaoshu")
    private String description;

    @TableField("shichang_fenzhong")
    private Integer durationMin;

    @TableField("jiage")
    private BigDecimal price;

    @TableField("zuida_renshu")
    private Integer maxCapacity;

    @TableField("dangqian_renshu")
    private Integer currentCapacity;

    @TableField("kaishi_shijian")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @TableField("jieshu_shijian")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("jiaoshi")
    private String room;

    @TableLogic
    private Integer deleted;
}
