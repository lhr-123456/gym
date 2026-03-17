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

    @TableId(value = "cert_id", type = IdType.AUTO)
    private Long certId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("cert_name")
    private String certName;

    @TableField("cert_type")
    private String certType;

    @TableField("cert_no")
    private String certNo;

    @TableField("issue_org")
    private String issueOrg;

    @TableField("issue_date")
    private LocalDate issueDate;

    @TableField("expire_date")
    private LocalDate expireDate;

    @TableField("cert_file_url")
    private String certFileUrl;

    @TableField("status")
    private Integer status;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
