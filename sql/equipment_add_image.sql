-- 已有库补充器材图片字段（与 EquipmentInfo.image 对应）
ALTER TABLE `equipment_info`
    ADD COLUMN `image` VARCHAR(500) DEFAULT NULL COMMENT '器材图片 URL' AFTER `location`;
