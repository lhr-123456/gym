package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("equipment_booking")
public class EquipmentBooking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "booking_id", type = IdType.AUTO)
    private Long bookingId;

    @TableField("member_id")
    private Long memberId;

    @TableField("equipment_id")
    private Long equipmentId;

    @TableField("booking_time")
    private LocalDateTime bookingTime;

    @TableField("return_time")
    private LocalDateTime returnTime;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
