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

    @TableId(value = "dengji_id", type = IdType.AUTO)
    private Long levelId;

    @TableField("dengji_mingcheng")
    private String levelName;

    @TableField("dengji_daima")
    private String levelCode;

    @TableField("paixu")
    private Integer levelOrder;

    @TableField("zhekou")
    private BigDecimal discountRate;

    @TableField("jifen_bili")
    private BigDecimal pointsRate;

    @TableField("zuidi_jifen")
    private Integer minPoints;

    @TableField("zuigao_jifen")
    private Integer maxPoints;

    @TableField("kate_jiage")
    private Double cardFee;

    @TableField("miaoshu")
    private String description;

    @TableField("tubiao")
    private String icon;

    @TableField("tubiao_yanse")
    private String iconColor;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
