package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coach_certificate")
public class CoachCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "zhengshu_id", type = IdType.AUTO)
    private Long certId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("zhengshu_ming")
    private String certName;

    @TableField("zhengshu_leixing")
    private String certType;

    @TableField("zhengshu_bianhao")
    private String certNo;

    @TableField("faxing_jigou")
    private String issueOrg;

    @TableField("faxing_riqi")
    private LocalDate issueDate;

    @TableField("youxiao_qixian")
    private LocalDate expireDate;

    @TableField("zhengjian_wenjian")
    private String certFileUrl;

    @TableField("zhuangtai")
    private Integer status;

    @TableField("jujue_yuanyin")
    private String rejectReason;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
