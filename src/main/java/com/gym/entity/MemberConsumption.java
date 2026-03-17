package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("member_consumption")
public class MemberConsumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "consumption_id", type = IdType.AUTO)
    private Long consumptionId;

    @TableField("member_id")
    private Long memberId;

    @TableField("order_no")
    private String orderNo;

    @TableField("consumption_type")
    private String consumptionType;

    @TableField("consumption_type_name")
    private String consumptionTypeName;

    @TableField("amount")
    private Double amount;

    @TableField("actual_amount")
    private Double actualAmount;

    @TableField("discount_amount")
    private Double discountAmount;

    @TableField("points_deducted")
    private Integer pointsDeducted;

    @TableField("points_earned")
    private Integer pointsEarned;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("consumption_time")
    private LocalDateTime consumptionTime;

    @TableField("related_id")
    private Long relatedId;

    @TableField("related_name")
    private String relatedName;

    @TableField("coach_id")
    private Long coachId;

    @TableField("coach_name")
    private String coachName;

    @TableField("status")
    private Integer status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
