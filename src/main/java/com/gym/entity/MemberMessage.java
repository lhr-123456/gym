package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("member_message")
public class MemberMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "xiaoxi_id", type = IdType.AUTO)
    private Long messageId;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("leixing")
    private String type;

    @TableField("biaoti")
    private String title;

    @TableField("neirong")
    private String content;

    @TableField("guanlian_id")
    private String refId;

    @TableField("guanlian_leixing")
    private String refType;

    @TableField("yidu")
    private Integer isRead;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "gengxin_shijian", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
