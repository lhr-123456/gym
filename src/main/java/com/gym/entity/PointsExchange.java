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

    /** 会员ID */
    @TableField("member_id")
    private Long memberId;

    /** 商品ID（实物商品时） */
    @TableField("goods_id")
    private Long goodsId;

    /** 关联ID（课程ID等） */
    @TableField("ref_id")
    private Long refId;

    /** 商品/课程名称 */
    @TableField("goods_name")
    private String goodsName;

    /** 消耗积分 */
    @TableField("points")
    private Integer points;

    /** 状态：0=待处理，1=已完成，2=已取消 */
    @TableField("status")
    private Integer status;

    /** 兑换时间 */
    @TableField(value = "exchange_time", fill = FieldFill.INSERT)
    private LocalDateTime exchangeTime;

    @TableLogic
    private Integer deleted;
}
