-- 课程分类表
CREATE TABLE IF NOT EXISTS `course_category` (
  `category_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- 插入默认课程分类
INSERT INTO `course_category` (`category_name`, `description`, `sort_order`, `status`) VALUES
('有氧课程', '有氧运动类课程，如跑步、动感单车等', 1, 1),
('力量训练', '力量训练类课程，如哑铃、杠铃等', 2, 1),
('瑜伽/普拉提', '瑜伽和普拉提类课程', 3, 1),
('舞蹈课程', '舞蹈类课程，如爵士舞、拉丁舞等', 4, 1),
('私教课程', '一对一私教课程', 5, 1);

-- 修改 course_info 表，添加 category_id 字段关联课程分类
ALTER TABLE `course_info` ADD COLUMN `category_id` BIGINT DEFAULT NULL COMMENT '分类ID' AFTER `course_type`;

-- 添加外键约束（可选）
-- ALTER TABLE `course_info` ADD CONSTRAINT `fk_course_info_category` FOREIGN KEY (`category_id`) REFERENCES `course_category`(`category_id`);
