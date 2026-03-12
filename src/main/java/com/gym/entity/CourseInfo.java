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

    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    @TableField("course_name")
    private String courseName;

    @TableField("coach_id")
    private Long coachId;

    @TableField(exist = false)
    private String coachName;

    @TableField("course_type")
    private String courseType;

    @TableField("category_id")
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;

    @TableField("description")
    private String description;

    @TableField("duration_min")
    private Integer durationMin;

    @TableField("price")
    private BigDecimal price;

    @TableField("max_capacity")
    private Integer maxCapacity;

    @TableField("current_capacity")
    private Integer currentCapacity;

    @TableField("start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @TableField("end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @TableField("status")
    private Integer status;

    @TableField("room")
    private String room;

    @TableLogic
    private Integer deleted;
}
