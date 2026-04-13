-- 会员消息表（支持课程提醒、教练回复、系统通知等场景）
CREATE TABLE IF NOT EXISTS `member_message` (
  `message_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
  `member_id` BIGINT NOT NULL COMMENT '所属会员ID',
  `type` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '消息类型：system/course/coach',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT COMMENT '消息内容',
  `ref_id` VARCHAR(50) DEFAULT NULL COMMENT '关联业务ID（bookingId/reviewId等）',
  `ref_type` VARCHAR(50) DEFAULT NULL COMMENT '关联业务类型（course_booking/course_review等）',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删，1=已删',
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员消息表';

-- 初始化示例数据（可选，根据需要执行）
-- INSERT INTO `member_message` (`member_id`, `type`, `title`, `content`, `is_read`) VALUES
-- (1, 'system', '欢迎使用健身管理系统', '您好，欢迎来到健身管理系统，祝您训练愉快！', 0),
-- (1, 'course', '您有新的课程预约', '您已成功预约课程，请按时参加。', 0),
-- (1, 'coach', '教练已回复您的评价', '教练回复：继续保持，加油！', 0);
