package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("member_consumption")
public class MemberConsumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "xiaofei_id", type = IdType.AUTO)
    private Long consumptionId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("dingdan_bianhao")
    private String orderNo;

    @TableField("xiaofei_leixing")
    private String consumptionType;

    @TableField("xiaofei_leixing_ming")
    private String consumptionTypeName;

    @TableField("jine")
    private Double amount;

    @TableField("shiji_jine")
    private Double actualAmount;

    @TableField("youhui_jine")
    private Double discountAmount;

    @TableField("jifen_koujian")
    private Integer pointsDeducted;

    @TableField("jifen_zengsong")
    private Integer pointsEarned;

    @TableField("fukuan_fangshi")
    private String paymentMethod;

    @TableField("xiaofei_shijian")
    private LocalDateTime consumptionTime;

    @TableField("guanlian_id")
    private Long relatedId;

    @TableField("guanlian_ming")
    private String relatedName;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("jiaolian_xingming")
    private String coachName;

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
