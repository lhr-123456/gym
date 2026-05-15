package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_salary")
public class CoachSalary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "gongzi_id", type = IdType.AUTO)
    private Long salaryId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("gongzi_yuefen")
    private String salaryMonth;

    @TableField("jibengongzi")
    private BigDecimal baseSalary;

    @TableField("keshi_shu")
    private BigDecimal classHours;

    @TableField("dan_ke_jine")
    private BigDecimal classFee;

    @TableField("keshi_zongji")
    private BigDecimal classTotal;

    @TableField("xiaoshou_jine")
    private BigDecimal salesAmount;

    @TableField("ticheng_bili")
    private BigDecimal commissionRate;

    @TableField("ticheng_jine")
    private BigDecimal commission;

    @TableField("jiangjin")
    private BigDecimal bonus;

    @TableField("faqian")
    private BigDecimal penalty;

    @TableField("yingfa_zongji")
    private BigDecimal totalSalary;

    @TableField("fafang_zhuangtai")
    private Integer paymentStatus;

    @TableField("fafang_riqi")
    private LocalDate paymentDate;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
