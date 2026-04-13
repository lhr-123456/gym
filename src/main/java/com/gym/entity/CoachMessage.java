package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_message")
public class CoachMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("member_id")
    private Long memberId;

    @TableField(exist = false)
    private String memberName;

    @TableField("content")
    private String content;

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
