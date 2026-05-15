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

    @TableId(value = "qicai_id", type = IdType.AUTO)
    private Long equipmentId;

    @TableField("qicai_ming")
    private String equipmentName;

    @TableField("leixing")
    private String type;

    @TableField("pinpai")
    private String brand;

    @TableField("xinghao")
    private String model;

    @TableField("zhuangtai")
    private String status;

    @TableField("goumai_riqi")
    private LocalDate purchaseDate;

    @TableField("zuihou_weixiu")
    private LocalDateTime lastMaintain;

    @TableField("xiayou_weixiu")
    private LocalDateTime nextMaintain;

    @TableField("miaoshu")
    private String description;

    @TableField("weizhi")
    private String location;

    @TableField("tupian")
    private String image;

    @TableLogic
    private Integer deleted;
}
