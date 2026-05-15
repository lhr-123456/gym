package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("member_points_record")
public class MemberPointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("huiyuan_id")
    private Long memberId;

    @TableField("renwu_leixing")
    private String taskType;

    @TableField("renwu_ming")
    private String taskName;

    @TableField("jifen")
    private Integer points;

    @TableField("yewu_id")
    private String bizId;

    @TableField("yewu_leixing")
    private String bizType;

    @TableField("beizhu")
    private String remark;

    @TableField(value = "chuangjian_shijian", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
