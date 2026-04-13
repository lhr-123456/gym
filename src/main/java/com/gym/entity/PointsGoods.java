package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("points_goods")
public class PointsGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    @TableField("name")
    private String name;

    /** 商品描述 */
    @TableField("description")
    private String description;

    /** 所需积分 */
    @TableField("points")
    private Integer points;

    /** 库存数量 */
    @TableField("stock")
    private Integer stock;

    /** 商品图片路径 */
    @TableField("image")
    private String image;

    /** 商品类型：goods=实物商品，course=课程 */
    @TableField("type")
    private String type;

    /** 关联ID（如课程ID） */
    @TableField("ref_id")
    private Long refId;

    /** 状态：1=上架，0=下架 */
    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
