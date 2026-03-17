package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_salary")
public class CoachSalary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "salary_id", type = IdType.AUTO)
    private Long salaryId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("salary_month")
    private String salaryMonth;

    @TableField("base_salary")
    private java.math.BigDecimal baseSalary;

    @TableField("class_hours")
    private java.math.BigDecimal classHours;

    @TableField("class_fee")
    private java.math.BigDecimal classFee;

    @TableField("class_total")
    private java.math.BigDecimal classTotal;

    @TableField("sales_amount")
    private java.math.BigDecimal salesAmount;

    @TableField("commission_rate")
    private java.math.BigDecimal commissionRate;

    @TableField("commission")
    private java.math.BigDecimal commission;

    @TableField("bonus")
    private java.math.BigDecimal bonus;

    @TableField("penalty")
    private java.math.BigDecimal penalty;

    @TableField("total_salary")
    private java.math.BigDecimal totalSalary;

    @TableField("payment_status")
    private Integer paymentStatus;

    @TableField("payment_date")
    private LocalDate paymentDate;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
