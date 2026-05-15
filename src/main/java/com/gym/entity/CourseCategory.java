package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_category")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "fenlei_id", type = IdType.AUTO)
    private Long categoryId;

    @TableField("fenlei_ming")
    private String categoryName;

    @TableField("miaoshu")
    private String description;

    @TableField("paixu")
    private Integer sortOrder;

    @TableField("zhuangtai")
    private Integer status;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
