package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("member_message")
public class MemberMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    @TableField("member_id")
    private Long memberId;

    /**
     * 消息类型：system / course / coach
     */
    @TableField("type")
    private String type;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 关联业务ID（如 bookingId / reviewId）
     */
    @TableField("ref_id")
    private String refId;

    /**
     * 关联业务类型（course_booking / course_review）
     */
    @TableField("ref_type")
    private String refType;

    /**
     * 是否已读：0=未读，1=已读
     */
    @TableField("is_read")
    private Integer isRead;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
