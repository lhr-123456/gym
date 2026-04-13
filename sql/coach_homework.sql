-- 教练课后作业表
CREATE TABLE IF NOT EXISTS `coach_homework` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
  `coach_id` BIGINT NOT NULL COMMENT '教练ID',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `course_booking_id` BIGINT DEFAULT NULL COMMENT '关联课程预约ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
  `content` TEXT NOT NULL COMMENT '作业内容',
  `target_date` DATE DEFAULT NULL COMMENT '目标完成日期',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0未完成1已完成)',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `coach_remark` VARCHAR(500) DEFAULT NULL COMMENT '教练备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  INDEX `idx_coach_id` (`coach_id`),
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练课后作业表';
