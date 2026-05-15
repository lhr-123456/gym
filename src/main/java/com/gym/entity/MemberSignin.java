package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member_signin")
public class MemberSignin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "qiandao_id", type = IdType.AUTO)
    private Long signinId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("qiandao_riqi")
    private LocalDate signinDate;

    @TableField("qiandao_shijian")
    private LocalDateTime signinTime;

    @TableField("qiandao_leixing")
    private String signinType;

    @TableField("jifen_zengsong")
    private Integer pointsEarned;

    @TableField("lianxu_tianshu")
    private Integer consecutiveDays;

    @TableField("beizhu")
    private String remarks;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
