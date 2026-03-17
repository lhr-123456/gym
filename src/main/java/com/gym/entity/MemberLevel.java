package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level")
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "level_id", type = IdType.AUTO)
    private Long levelId;

    @TableField("level_name")
    private String levelName;

    @TableField("level_code")
    private String levelCode;

    @TableField("level_order")
    private Integer levelOrder;

    @TableField("discount_rate")
    private BigDecimal discountRate;

    @TableField("points_rate")
    private BigDecimal pointsRate;

    @TableField("min_points")
    private Integer minPoints;

    @TableField("max_points")
    private Integer maxPoints;

    @TableField("card_fee")
    private Double cardFee;

    @TableField("description")
    private String description;

    @TableField("icon")
    private String icon;

    @TableField("icon_color")
    private String iconColor;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
