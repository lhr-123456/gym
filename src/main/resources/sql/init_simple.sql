CREATE DATABASE IF NOT EXISTS gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gym_db;

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `user_type` INT NOT NULL DEFAULT 3,
    `member_id` BIGINT DEFAULT NULL,
    `coach_id` BIGINT DEFAULT NULL,
    `status` INT NOT NULL DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_coach_id` (`coach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `member_info`;
CREATE TABLE `member_info` (
    `member_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_name` VARCHAR(50) NOT NULL,
    `gender` CHAR(1) DEFAULT NULL,
    `birth_date` DATE DEFAULT NULL,
    `phone_num` VARCHAR(20) NOT NULL,
    `email_addr` VARCHAR(100) DEFAULT NULL,
    `reg_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `fitness_level` VARCHAR(20) DEFAULT 'Beginner',
    `last_visit` DATETIME DEFAULT NULL,
    `account_status` INT NOT NULL DEFAULT 0,
    `member_level` INT DEFAULT 1,
    `points` INT DEFAULT 0,
    `balance` DECIMAL(10,2) DEFAULT 0.00,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `uk_phone_num` (`phone_num`),
    KEY `idx_member_name` (`member_name`),
    KEY `idx_fitness_level` (`fitness_level`),
    KEY `idx_account_status` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `coach_info`;
CREATE TABLE `coach_info` (
    `coach_id` BIGINT NOT NULL AUTO_INCREMENT,
    `coach_name` VARCHAR(50) NOT NULL,
    `gender` CHAR(1) DEFAULT NULL,
    `birth_date` DATE DEFAULT NULL,
    `phone_num` VARCHAR(20) NOT NULL,
    `email_addr` VARCHAR(100) DEFAULT NULL,
    `specialty` VARCHAR(100) DEFAULT NULL,
    `experience_years` INT DEFAULT 0,
    `certification` VARCHAR(255) DEFAULT NULL,
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `status` INT NOT NULL DEFAULT 1,
    `hire_date` DATE DEFAULT NULL,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`coach_id`),
    UNIQUE KEY `uk_phone_num` (`phone_num`),
    KEY `idx_coach_name` (`coach_name`),
    KEY `idx_specialty` (`specialty`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `course_info`;
CREATE TABLE `course_info` (
    `course_id` BIGINT NOT NULL AUTO_INCREMENT,
    `course_name` VARCHAR(100) NOT NULL,
    `coach_id` BIGINT DEFAULT NULL,
    `course_type` VARCHAR(20) NOT NULL,
    `description` TEXT,
    `duration_min` INT DEFAULT 60,
    `price` DECIMAL(10,2) DEFAULT 0.00,
    `max_capacity` INT NOT NULL DEFAULT 10,
    `current_capacity` INT DEFAULT 0,
    `start_time` DATETIME DEFAULT NULL,
    `end_time` DATETIME DEFAULT NULL,
    `status` INT NOT NULL DEFAULT 0,
    `room` VARCHAR(50) DEFAULT NULL,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_course_type` (`course_type`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `course_booking`;
CREATE TABLE `course_booking` (
    `booking_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `coach_id` BIGINT DEFAULT NULL,
    `booking_time` DATETIME NOT NULL,
    `class_time` DATETIME NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'Booked',
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`booking_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_class_time` (`class_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `equipment_info`;
CREATE TABLE `equipment_info` (
    `equipment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `equipment_name` VARCHAR(100) NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `brand` VARCHAR(50) DEFAULT NULL,
    `model` VARCHAR(50) DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'Available',
    `purchase_date` DATE DEFAULT NULL,
    `last_maintain` DATETIME DEFAULT NULL,
    `next_maintain` DATETIME DEFAULT NULL,
    `description` TEXT,
    `location` VARCHAR(100) DEFAULT NULL,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`equipment_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_purchase_date` (`purchase_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `equipment_booking`;
CREATE TABLE `equipment_booking` (
    `booking_id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `equipment_id` BIGINT NOT NULL,
    `booking_time` DATETIME NOT NULL,
    `return_time` DATETIME DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'Borrowing',
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`booking_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_equipment_id` (`equipment_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `user_info` (`username`, `password`, `user_type`, `status`) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS', 1, 1);

INSERT INTO `member_info` (`member_name`, `gender`, `birth_date`, `phone_num`, `email_addr`, `fitness_level`, `member_level`, `points`, `balance`) VALUES ('Zhang San', 'M', '1990-01-15', '13800138001', 'zhangsan@example.com', 'Intermediate', 2, 1000, 500.00), ('Li Si', 'F', '1992-05-20', '13800138002', 'lisi@example.com', 'Beginner', 1, 500, 200.00), ('Wang Wu', 'M', '1988-08-10', '13800138003', 'wangwu@example.com', 'Advanced', 3, 2000, 1000.00);

INSERT INTO `coach_info` (`coach_name`, `gender`, `birth_date`, `phone_num`, `email_addr`, `specialty`, `experience_years`, `certification`, `status`) VALUES ('Coach Zhao', 'M', '1985-03-12', '13900139001', 'zhao@gym.com', 'Weight Loss', 5, 'ACE Certified', 1), ('Coach Qian', 'F', '1990-07-25', '13900139002', 'qian@gym.com', 'Yoga Pilates', 3, 'RYT Certified', 1);

INSERT INTO `course_info` (`course_name`, `coach_id`, `course_type`, `description`, `duration_min`, `price`, `max_capacity`, `current_capacity`, `start_time`, `end_time`, `status`, `room`) VALUES ('Weight Loss Training', 1, 'Group', 'High intensity interval training', 60, 100.00, 20, 5, '2024-01-15 10:00:00', '2024-01-15 11:00:00', 0, 'Room 1'), ('Yoga Basic', 2, 'Group', 'Yoga basic course for beginners', 90, 80.00, 15, 8, '2024-01-15 14:00:00', '2024-01-15 15:30:00', 0, 'Yoga Room'), ('Personal Muscle Building', 1, 'Private', 'One-on-one muscle building training', 60, 300.00, 1, 0, '2024-01-16 09:00:00', '2024-01-16 10:00:00', 0, 'Weight Area');

INSERT INTO `equipment_info` (`equipment_name`, `type`, `brand`, `model`, `status`, `purchase_date`, `last_maintain`, `next_maintain`, `location`) VALUES ('Treadmill', 'Cardio', 'TechnoGym', 'Run 500', 'Available', '2023-01-10', '2024-01-01 10:00:00', '2024-04-01 10:00:00', 'Cardio Area 1'), ('Elliptical', 'Cardio', 'LifeFitness', 'E5', 'Available', '2023-02-15', '2024-01-05 14:00:00', '2024-04-05 14:00:00', 'Cardio Area 2'), ('Smith Machine', 'Strength', 'Hammer Strength', 'Smith Pro', 'Available', '2023-03-20', '2024-01-03 09:00:00', '2024-04-03 09:00:00', 'Weight Area 1'), ('Dumbbell Set', 'Strength', 'Rogue', 'Hex Dumbbell Set', 'Available', '2023-01-05', '2024-01-02 11:00:00', '2024-04-02 11:00:00', 'Weight Area 2');
