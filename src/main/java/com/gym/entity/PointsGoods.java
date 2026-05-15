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

    @TableField("mingcheng")
    private String name;

    @TableField("miaoshu")
    private String description;

    @TableField("jifen")
    private Integer points;

    @TableField("kucun")
    private Integer stock;

    @TableField("tupian")
    private String image;

    @TableField("leixing")
    private String type;

    @TableField("ref_id")
    private Long refId;

    @TableField("zhuangtai")
    private Integer status;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
