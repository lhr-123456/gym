-- 教练消息表（会员联系教练、教练回复）
CREATE TABLE IF NOT EXISTS `coach_message` (
  `message_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
  `coach_id` BIGINT NOT NULL COMMENT '教练ID',
  `member_id` BIGINT NOT NULL COMMENT '发送会员ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删，1=已删',
  INDEX `idx_coach_id` (`coach_id`),
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练消息表';
