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

    @TableId(value = "tice_id", type = IdType.AUTO)
    private Long testId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("tice_riqi")
    private LocalDate testDate;

    @TableField("shengao")
    private Double height;

    @TableField("tizhong")
    private Double weight;

    @TableField("tizhi_bmi")
    private Double bmi;

    @TableField("zhifang_bilv")
    private Double bodyFatRate;

    @TableField("jirou_zhiliang")
    private Double muscleMass;

    @TableField("tinei_shuifen")
    private Double waterContent;

    @TableField("neizang_zhifang")
    private Double visceralFat;

    @TableField("jichu_xindai")
    private Integer basalMetabolism;

    @TableField("xiongwei")
    private Double chest;

    @TableField("yaowei")
    private Double waist;

    @TableField("tunwei")
    private Double hip;

    @TableField("zuo_bibi")
    private Double leftArm;

    @TableField("you_bibi")
    private Double rightArm;

    @TableField("zuo_tuiwei")
    private Double leftLeg;

    @TableField("you_tuiwei")
    private Double rightLeg;

    @TableField("xinshuai")
    private Integer heartRate;

    @TableField("shousuoya")
    private Integer bloodPressureHigh;

    @TableField("shuzhangya")
    private Integer bloodPressureLow;

    @TableField("jiankang_pingfen")
    private Integer healthScore;

    @TableField("tice_jieguo")
    private String testResult;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
