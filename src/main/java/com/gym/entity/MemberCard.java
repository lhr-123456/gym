package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_card")
public class MemberCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "card_id", type = IdType.AUTO)
    private Long cardId;

    @TableField("card_no")
    private String cardNo;

    @TableField("member_id")
    private Long memberId;

    @TableField("card_type")
    private String cardType;

    @TableField("card_type_name")
    private String cardTypeName;

    @TableField("total_times")
    private Integer totalTimes;

    @TableField("used_times")
    private Integer usedTimes;

    @TableField("remaining_times")
    private Integer remainingTimes;

    @TableField("total_amount")
    private Double totalAmount;

    @TableField("balance")
    private Double balance;

    @TableField("purchase_date")
    private LocalDate purchaseDate;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

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
