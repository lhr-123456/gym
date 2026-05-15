package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_message")
public class CoachMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "xiaoxi_id", type = IdType.AUTO)
    private Long messageId;

    @TableField("jiaolian_id")
    private Long coachId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField(exist = false)
    private String memberName;

    @TableField("neirong")
    private String content;

    @TableField("yidu")
    private Integer isRead;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
