-- ===============================================
-- 积分商城数据库表
-- ===============================================

-- 积分商品表（支持实物商品和课程两类）
CREATE TABLE IF NOT EXISTS `points_goods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品/课程名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
    `points` INT NOT NULL DEFAULT 0 COMMENT '所需积分',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量（实物商品用）',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片路径',
    `type` VARCHAR(20) NOT NULL DEFAULT 'goods' COMMENT '类型：goods=实物商品，course=课程',
    `ref_id` BIGINT DEFAULT NULL COMMENT '关联ID（如课程ID）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=上架，0=下架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商品表';

-- 积分兑换记录表
CREATE TABLE IF NOT EXISTS `points_exchange` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `goods_id` BIGINT DEFAULT NULL COMMENT '商品ID（实物商品时）',
    `ref_id` BIGINT DEFAULT NULL COMMENT '关联ID（课程ID等）',
    `goods_name` VARCHAR(100) NOT NULL COMMENT '商品/课程名称',
    `points` INT NOT NULL DEFAULT 0 COMMENT '消耗积分',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=待处理，1=已完成，2=已取消',
    `exchange_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_exchange_time` (`exchange_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换记录表';

-- ===============================================
-- 初始化积分商品数据（可选，执行以下 INSERT 前请先建表）
-- ===============================================

-- INSERT INTO `points_goods` (`name`, `description`, `points`, `stock`, `image`, `type`, `status`) VALUES
-- ('健身手套', '透气防滑专业健身手套', 200, 15, 'gympicture/gymshoutao.jpeg', 'goods', 1),
-- ('运动水壶', '便携式运动水壶 600ml', 150, 20, 'gympicture/gymshuihu.jpeg', 'goods', 1),
-- ('瑜伽垫', '加厚防滑瑜伽垫', 300, 8, 'gympicture/gymyujiadian.jpeg', 'goods', 1),
-- ('运动毛巾', '速干运动毛巾', 100, 30, 'gympicture/gymmaojin.jpeg', 'goods', 1),
-- ('健身包', '大容量运动健身包', 450, 5, 'gympicture/gymjianshenbao.jpeg', 'goods', 1),
-- ('跳绳', '专业计数跳绳', 180, 12, 'gympicture/gymtiaosheng.jpeg', 'goods', 1);
