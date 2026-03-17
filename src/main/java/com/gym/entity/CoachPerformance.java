package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("coach_performance")
public class CoachPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "perf_id", type = IdType.AUTO)
    private Long perfId;

    @TableField("coach_id")
    private Long coachId;

    @TableField("eval_month")
    private String evalMonth;

    @TableField("attendance_score")
    private java.math.BigDecimal attendanceScore;

    @TableField("teaching_score")
    private java.math.BigDecimal teachingScore;

    @TableField("service_score")
    private java.math.BigDecimal serviceScore;

    @TableField("sales_score")
    private java.math.BigDecimal salesScore;

    @TableField("student_feedback")
    private java.math.BigDecimal studentFeedback;

    @TableField("total_score")
    private java.math.BigDecimal totalScore;

    @TableField("eval_level")
    private String evalLevel;

    @TableField("eval_by")
    private Long evalBy;

    @TableField("eval_time")
    private LocalDateTime evalTime;

    @TableField("eval_remarks")
    private String evalRemarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
