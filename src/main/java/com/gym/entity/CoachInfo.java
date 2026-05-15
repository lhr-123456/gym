package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("coach_info")
public class CoachInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "jiaolian_id", type = IdType.AUTO)
    @JsonProperty("coachId")
    private Long coachId;

    @TableField("jiaolian_xingming")
    @JsonProperty("coachName")
    private String coachName;

    @TableField("xingbie")
    @JsonProperty("gender")
    private String gender;

    @TableField("chusheng_riqi")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @TableField("shouji_haoma")
    @JsonProperty("phoneNum")
    private String phoneNum;

    @TableField("youxiang")
    @JsonProperty("emailAddr")
    private String emailAddr;

    @TableField("zhuanye")
    @JsonProperty("specialty")
    private String specialty;

    @TableField("gongzuo_nianxian")
    @JsonProperty("experienceYears")
    private Integer experienceYears;

    @TableField("zige_zheng")
    @JsonProperty("certification")
    private String certification;

    @TableField("touxiang_url")
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @TableField("zhuangtai")
    @JsonProperty("status")
    private Integer status;

    @TableField(value = "ruzhi_riqi", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @JsonProperty("hireDate")
    private LocalDate hireDate;

    @TableLogic
    @JsonProperty("deleted")
    private Integer deleted;
}
