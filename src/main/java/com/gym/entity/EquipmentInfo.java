package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("equipment_info")
public class EquipmentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "equipment_id", type = IdType.AUTO)
    private Long equipmentId;

    @TableField("equipment_name")
    private String equipmentName;

    @TableField("type")
    private String type;

    @TableField("brand")
    private String brand;

    @TableField("model")
    private String model;

    @TableField("status")
    private String status;

    @TableField("purchase_date")
    private LocalDate purchaseDate;

    @TableField("last_maintain")
    private LocalDateTime lastMaintain;

    @TableField("next_maintain")
    private LocalDateTime nextMaintain;

    @TableField("description")
    private String description;

    @TableField("location")
    private String location;

    @TableLogic
    private Integer deleted;
}
