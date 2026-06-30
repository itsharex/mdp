-- =========================================================================
-- 迁移脚本：通知加密方案从 RSA 迁移至 Token + EncodingAESKey 对称加密
-- 日期：2026-06-29
-- 说明：
--   1. mdo_app_keys：删除 RSA 相关字段，新增对称加密字段
--   2. mdo_event_push：新增加密配置冗余字段（避免推送时再查 AppKeys）
--   3. mdo_notify_info：新增加密配置冗余字段（避免回调时再查 AppKeys）
-- 注意：执行前请先备份相关表数据
-- =========================================================================

-- ------------------------------------
-- 1. mdo_app_keys：RSA -> 对称加密
-- ------------------------------------

-- 1.1 删除 RSA 相关字段
ALTER TABLE `mdo_app_keys` DROP COLUMN `key_format`;
ALTER TABLE `mdo_app_keys` DROP COLUMN `public_key_app`;
ALTER TABLE `mdo_app_keys` DROP COLUMN `private_key_app`;
ALTER TABLE `mdo_app_keys` DROP COLUMN `public_key_platform`;
ALTER TABLE `mdo_app_keys` DROP COLUMN `private_key_platform`;

-- 1.2 新增对称加密字段
ALTER TABLE `mdo_app_keys`
    ADD COLUMN `notify_token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT '签名校验令牌，平台和开发者共同持有，用于生成和验证 signature / msg_signature'
        AFTER `notify_encryption_type`;

ALTER TABLE `mdo_app_keys`
    ADD COLUMN `notify_encoding_aes_key` varchar(43) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT 'AES加解密密钥（43字符），平台和开发者共同持有。生成方式：Base64Decode(encodingAesKey + "=") 得到32字节AESKey'
        AFTER `notify_token`;

-- 1.3 更新 notify_encryption_type 字段注释
ALTER TABLE `mdo_app_keys`
    MODIFY COLUMN `notify_encryption_type` smallint DEFAULT NULL
        COMMENT '加密模式，[0-明文模式 1-兼容模式 2-安全模式]';

-- ------------------------------------
-- 2. mdo_event_push：新增加密配置冗余字段
-- ------------------------------------

ALTER TABLE `mdo_event_push`
    ADD COLUMN `notify_encryption_type` smallint DEFAULT NULL
        COMMENT '加密模式（冗余自 AppKeys，避免推送时再查库），[0-明文模式 1-兼容模式 2-安全模式]'
        AFTER `notify_url`;

ALTER TABLE `mdo_event_push`
    ADD COLUMN `notify_token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT '签名校验令牌（冗余自 AppKeys）'
        AFTER `notify_encryption_type`;

ALTER TABLE `mdo_event_push`
    ADD COLUMN `notify_encoding_aes_key` varchar(43) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT 'AES加解密密钥（冗余自 AppKeys）'
        AFTER `notify_token`;

-- ------------------------------------
-- 3. mdo_notify_info：新增加密配置冗余字段
-- ------------------------------------

ALTER TABLE `mdo_notify_info`
    ADD COLUMN `notify_encryption_type` smallint DEFAULT NULL
        COMMENT '加密模式（冗余自 AppKeys，避免回调时再查库），[0-明文模式 1-兼容模式 2-安全模式]'
        AFTER `notify_url`;

ALTER TABLE `mdo_notify_info`
    ADD COLUMN `notify_token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT '签名校验令牌（冗余自 AppKeys）'
        AFTER `notify_encryption_type`;

ALTER TABLE `mdo_notify_info`
    ADD COLUMN `notify_encoding_aes_key` varchar(43) COLLATE utf8mb4_general_ci DEFAULT NULL
        COMMENT 'AES加解密密钥（冗余自 AppKeys）'
        AFTER `notify_token`;
