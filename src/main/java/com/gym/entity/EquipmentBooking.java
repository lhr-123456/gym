package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("equipment_booking")
public class EquipmentBooking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "yuyue_id", type = IdType.AUTO)
    private Long bookingId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("qicai_id")
    private Long equipmentId;

    @TableField("yuyue_shijian")
    private LocalDateTime bookingTime;

    @TableField("guihuan_shijian")
    private LocalDateTime returnTime;

    @TableField("zhuangtai")
    private String status;

    @TableField("beizhu")
    private String remark;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
