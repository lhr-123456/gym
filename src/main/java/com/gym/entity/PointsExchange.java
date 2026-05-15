package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("points_exchange")
public class PointsExchange implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("shangpin_id")
    private Long goodsId;

    @TableField("ref_id")
    private Long refId;

    @TableField("shangpin_ming")
    private String goodsName;

    @TableField("jifen")
    private Integer points;

    @TableField("zhuangtai")
    private Integer status;

    @TableField(value = "duihuan_shijian", fill = FieldFill.INSERT)
    private LocalDateTime exchangeTime;

    @TableLogic
    private Integer deleted;
}
