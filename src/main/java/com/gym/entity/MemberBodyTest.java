package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_body_test")
public class MemberBodyTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "test_id", type = IdType.AUTO)
    private Long testId;

    @TableField("member_id")
    private Long memberId;

    @TableField("test_date")
    private LocalDate testDate;

    @TableField("height")
    private Double height;

    @TableField("weight")
    private Double weight;

    @TableField("bmi")
    private Double bmi;

    @TableField("body_fat_rate")
    private Double bodyFatRate;

    @TableField("muscle_mass")
    private Double muscleMass;

    @TableField("water_content")
    private Double waterContent;

    @TableField("visceral_fat")
    private Double visceralFat;

    @TableField("basal_metabolism")
    private Integer basalMetabolism;

    @TableField("chest")
    private Double chest;

    @TableField("waist")
    private Double waist;

    @TableField("hip")
    private Double hip;

    @TableField("left_arm")
    private Double leftArm;

    @TableField("right_arm")
    private Double rightArm;

    @TableField("left_leg")
    private Double leftLeg;

    @TableField("right_leg")
    private Double rightLeg;

    @TableField("heart_rate")
    private Integer heartRate;

    @TableField("blood_pressure_high")
    private Integer bloodPressureHigh;

    @TableField("blood_pressure_low")
    private Integer bloodPressureLow;

    @TableField("health_score")
    private Integer healthScore;

    @TableField("test_result")
    private String testResult;

    @TableField("coach_id")
    private Long coachId;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
