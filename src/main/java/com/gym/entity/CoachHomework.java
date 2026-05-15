package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_homework")
public class CoachHomework implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("yuyue_id")
    private Long courseBookingId;

    @TableField("biaoti")
    private String title;

    @TableField("neirong")
    private String content;

    @TableField("mubiao_riqi")
    private LocalDate targetDate;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("wancheng_shijian")
    private LocalDateTime completeTime;

    @TableField("jiaolian_beizhu")
    private String coachRemark;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
