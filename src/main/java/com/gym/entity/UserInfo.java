package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "yonghu_id", type = IdType.AUTO)
    private Long userId;

    @TableField("yonghu_ming")
    private String username;

    @TableField("mi_ma")
    private String password;

    @TableField("yonghu_leixing")
    private Integer userType;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("zhuangtai")
    private Integer status;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
