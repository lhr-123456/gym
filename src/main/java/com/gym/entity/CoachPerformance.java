package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coach_performance")
public class CoachPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pingjia_id", type = IdType.AUTO)
    private Long perfId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("pinggu_yuefen")
    private String evalMonth;

    @TableField("chuqin_pingfen")
    private BigDecimal attendanceScore;

    @TableField("jiaoxue_pingfen")
    private BigDecimal teachingScore;

    @TableField("fuwu_pingfen")
    private BigDecimal serviceScore;

    @TableField("xiaoshou_pingfen")
    private BigDecimal salesScore;

    @TableField("xueyuan_fankui")
    private BigDecimal studentFeedback;

    @TableField("zongfen")
    private BigDecimal totalScore;

    @TableField("pingjia_dengji")
    private String evalLevel;

    @TableField("pinggu_ren")
    private Long evalBy;

    @TableField("pinggu_shijian")
    private LocalDateTime evalTime;

    @TableField("pinggu_beizhu")
    private String evalRemarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
