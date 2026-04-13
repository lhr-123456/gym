-- 会员积分明细表（积分获取/消耗流水）
CREATE TABLE IF NOT EXISTS `member_points_record` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
  `member_id` BIGINT NOT NULL COMMENT '会员ID',
  `task_type` VARCHAR(50) NOT NULL COMMENT '任务类型',
  `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `points` INT NOT NULL COMMENT '积分变动（正数为获取，负数为消耗）',
  `biz_id` VARCHAR(50) DEFAULT NULL COMMENT '关联业务ID',
  `biz_type` VARCHAR(50) DEFAULT NULL COMMENT '关联业务类型',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_task_type` (`task_type`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分明细表';

-- 初始化默认积分任务定义（Java 端用枚举或常量实现，此处仅作说明）
-- task_type 枚举值：
-- signin      - 每日签到  +10
-- complete_course - 完成课程  +20
-- booking     - 预约课程  +5
-- review      - 评价课程  +10
-- profile     - 完善资料  +50（一次性）
-- invite      - 邀请好友  +30（一次性）
