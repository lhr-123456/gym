-- 健身房管理系统数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gym_db;

-- 用户信息表
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `user_type` INT NOT NULL DEFAULT 3 COMMENT '用户类型 (1:管理员，2:教练，3:会员)',
    `member_id` BIGINT DEFAULT NULL COMMENT '会员 ID',
    `coach_id` BIGINT DEFAULT NULL COMMENT '教练 ID',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 (0:禁用，1:启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_coach_id` (`coach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 会员信息表
DROP TABLE IF EXISTS `member_info`;
CREATE TABLE `member_info` (
    `member_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员 ID',
    `member_name` VARCHAR(50) NOT NULL COMMENT '会员姓名',
    `gender` CHAR(1) DEFAULT NULL COMMENT '性别 (M:男，F:女)',
    `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
    `phone_num` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `email_addr` VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `reg_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `fitness_level` VARCHAR(20) DEFAULT '初级' COMMENT '健身水平 (初级/中级/高级)',
    `last_visit` DATETIME DEFAULT NULL COMMENT '最近访问时间',
    `account_status` INT NOT NULL DEFAULT 0 COMMENT '账户状态 (0:正常，1:冻结)',
    `member_level` INT DEFAULT 1 COMMENT '会员等级 (1:普通会员，2:银卡，3:金卡，4:钻石)',
    `points` INT DEFAULT 0 COMMENT '积分',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `uk_phone_num` (`phone_num`),
    KEY `idx_member_name` (`member_name`),
    KEY `idx_fitness_level` (`fitness_level`),
    KEY `idx_account_status` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信息表';

-- 教练信息表
DROP TABLE IF EXISTS `coach_info`;
CREATE TABLE `coach_info` (
    `coach_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教练 ID',
    `coach_name` VARCHAR(50) NOT NULL COMMENT '教练姓名',
    `gender` CHAR(1) DEFAULT NULL COMMENT '性别 (M:男，F:女)',
    `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
    `phone_num` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `email_addr` VARCHAR(100) DEFAULT NULL COMMENT '电子邮箱',
    `specialty` VARCHAR(100) DEFAULT NULL COMMENT '专长',
    `experience_years` INT DEFAULT 0 COMMENT '从业年限',
    `certification` VARCHAR(255) DEFAULT NULL COMMENT '资质证书',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态 (0:离职，1:在职)',
    `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`coach_id`),
    UNIQUE KEY `uk_phone_num` (`phone_num`),
    KEY `idx_coach_name` (`coach_name`),
    KEY `idx_specialty` (`specialty`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练信息表';

-- 课程信息表
DROP TABLE IF EXISTS `course_info`;
CREATE TABLE `course_info` (
    `course_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程 ID',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `coach_id` BIGINT DEFAULT NULL COMMENT '教练 ID',
    `course_type` VARCHAR(20) NOT NULL COMMENT '课程类型 (团课/私教课)',
    `description` TEXT COMMENT '课程描述',
    `duration_min` INT DEFAULT 60 COMMENT '课程时长 (分钟)',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '课程价格',
    `max_capacity` INT NOT NULL DEFAULT 10 COMMENT '最大容量',
    `current_capacity` INT DEFAULT 0 COMMENT '当前人数',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `status` INT NOT NULL DEFAULT 0 COMMENT '课程状态 (0:正常，1:已取消，2:已满员)',
    `room` VARCHAR(50) DEFAULT NULL COMMENT '教室',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_course_type` (`course_type`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程信息表';

-- 课程预约表
DROP TABLE IF EXISTS `course_booking`;
CREATE TABLE `course_booking` (
    `booking_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约 ID',
    `member_id` BIGINT NOT NULL COMMENT '会员 ID',
    `course_id` BIGINT NOT NULL COMMENT '课程 ID',
    `coach_id` BIGINT DEFAULT NULL COMMENT '教练 ID',
    `booking_time` DATETIME NOT NULL COMMENT '预约时间',
    `class_time` DATETIME NOT NULL COMMENT '课程时间',
    `status` VARCHAR(20) NOT NULL DEFAULT '已预约' COMMENT '预约状态 (已预约/已取消)',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`booking_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_class_time` (`class_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程预约表';

-- 器材信息表
DROP TABLE IF EXISTS `equipment_info`;
CREATE TABLE `equipment_info` (
    `equipment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '器材 ID',
    `equipment_name` VARCHAR(100) NOT NULL COMMENT '器材名称',
    `type` VARCHAR(20) NOT NULL COMMENT '器材类型 (有氧/力量/其他)',
    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    `model` VARCHAR(50) DEFAULT NULL COMMENT '型号',
    `status` VARCHAR(20) NOT NULL DEFAULT '可用' COMMENT '状态 (可用/维修中/已报废)',
    `purchase_date` DATE DEFAULT NULL COMMENT '购买日期',
    `last_maintain` DATETIME DEFAULT NULL COMMENT '最后维护日期',
    `next_maintain` DATETIME DEFAULT NULL COMMENT '下次维护日期',
    `description` TEXT COMMENT '描述',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '位置',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`equipment_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_purchase_date` (`purchase_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='器材信息表';

-- 器材借用表
DROP TABLE IF EXISTS `equipment_booking`;
CREATE TABLE `equipment_booking` (
    `booking_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '借用 ID',
    `member_id` BIGINT NOT NULL COMMENT '会员 ID',
    `equipment_id` BIGINT NOT NULL COMMENT '器材 ID',
    `booking_time` DATETIME NOT NULL COMMENT '借用时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '归还时间',
    `status` VARCHAR(20) NOT NULL DEFAULT '借用中' COMMENT '借用状态 (借用中/已归还)',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删除，1:已删除)',
    PRIMARY KEY (`booking_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='器材借用表';

-- 初始化数据

-- 插入管理员账户 (密码：admin123)
INSERT INTO `user_info` (`username`, `password`, `user_type`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', 1, 1);

-- 插入测试会员数据
INSERT INTO `member_info` (`member_name`, `gender`, `birth_date`, `phone_num`, `email_addr`, `fitness_level`, `member_level`, `points`, `balance`) 
VALUES 
('张三', 'M', '1990-01-15', '13800138001', 'zhangsan@example.com', '中级', 2, 1000, 500.00),
('李四', 'F', '1992-05-20', '13800138002', 'lisi@example.com', '初级', 1, 500, 200.00),
('王五', 'M', '1988-08-10', '13800138003', 'wangwu@example.com', '高级', 3, 2000, 1000.00);

-- 插入测试教练数据
INSERT INTO `coach_info` (`coach_name`, `gender`, `birth_date`, `phone_num`, `email_addr`, `specialty`, `experience_years`, `certification`, `status`) 
VALUES 
('赵教练', 'M', '1985-03-12', '13900139001', 'zhao@gym.com', '减脂塑形', 5, 'ACE 认证教练', 1),
('钱教练', 'F', '1990-07-25', '13900139002', 'qian@gym.com', '瑜伽普拉提', 3, 'RYT 认证教练', 1);

-- 插入测试课程数据
INSERT INTO `course_info` (`course_name`, `coach_id`, `course_type`, `description`, `duration_min`, `price`, `max_capacity`, `current_capacity`, `start_time`, `end_time`, `status`, `room`) 
VALUES 
('减脂训练营', 1, '团课', '高强度间歇训练，帮助快速减脂', 60, 100.00, 20, 5, '2024-01-15 10:00:00', '2024-01-15 11:00:00', 0, '团操房 1'),
('瑜伽基础', 2, '团课', '适合初学者的瑜伽基础课程', 90, 80.00, 15, 8, '2024-01-15 14:00:00', '2024-01-15 15:30:00', 0, '瑜伽室'),
('私人增肌训练', 1, '私教课', '一对一增肌指导', 60, 300.00, 1, 0, '2024-01-16 09:00:00', '2024-01-16 10:00:00', 0, '力量区');

-- 插入测试器材数据
INSERT INTO `equipment_info` (`equipment_name`, `type`, `brand`, `model`, `status`, `purchase_date`, `last_maintain`, `next_maintain`, `location`) 
VALUES 
('跑步机', '有氧', 'TechnoGym', 'Run 500', '可用', '2023-01-10', '2024-01-01 10:00:00', '2024-04-01 10:00:00', '有氧区 1 号'),
('椭圆机', '有氧', 'LifeFitness', 'E5', '可用', '2023-02-15', '2024-01-05 14:00:00', '2024-04-05 14:00:00', '有氧区 2 号'),
('史密斯机', '力量', 'Hammer Strength', 'Smith Pro', '可用', '2023-03-20', '2024-01-03 09:00:00', '2024-04-03 09:00:00', '力量区 1 号'),
('哑铃套装', '力量', 'Rogue', 'Hex Dumbbell Set', '可用', '2023-01-05', '2024-01-02 11:00:00', '2024-04-02 11:00:00', '力量区 2 号');
