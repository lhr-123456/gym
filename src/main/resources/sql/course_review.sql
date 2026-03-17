-- 课程评价表
CREATE TABLE IF NOT EXISTS `course_review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `coach_id` BIGINT DEFAULT NULL COMMENT '教练ID',
  `rating` INT NOT NULL COMMENT '评分: 1-5',
  `content` TEXT COMMENT '评价内容',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
  `reply` TEXT COMMENT '回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`review_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';

-- 修改 course_booking 表，添加签到时间字段
ALTER TABLE `course_booking` ADD COLUMN `signin_time` DATETIME DEFAULT NULL COMMENT '签到时间' AFTER `remark`;
