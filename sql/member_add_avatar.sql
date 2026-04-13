-- 已有库补充会员头像字段（与 MemberInfo.avatar 对应）
ALTER TABLE `member_info`
    ADD COLUMN `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像 URL' AFTER `balance`;
