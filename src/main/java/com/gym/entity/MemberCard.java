package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_card")
public class MemberCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "kate_id", type = IdType.AUTO)
    private Long cardId;

    @TableField("kahao")
    private String cardNo;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("kate_leixing")
    private String cardType;

    @TableField("kate_leixing_ming")
    private String cardTypeName;

    @TableField("zong_cishu")
    private Integer totalTimes;

    @TableField("yishiyong_cishu")
    private Integer usedTimes;

    @TableField("shengyu_cishu")
    private Integer remainingTimes;

    @TableField("zongjine")
    private Double totalAmount;

    @TableField("yue_e")
    private Double balance;

    @TableField("goumai_riqi")
    private LocalDate purchaseDate;

    @TableField("youxiao_kaishi")
    private LocalDate startDate;

    @TableField("youxiao_jieshu")
    private LocalDate endDate;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
