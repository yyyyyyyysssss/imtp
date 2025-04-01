SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for im_authority
-- ----------------------------
DROP TABLE IF EXISTS `im_authority`;
CREATE TABLE `im_authority`  (
                                 `id` bigint NOT NULL,
                                 `parent_id` bigint NULL DEFAULT NULL COMMENT '父节点id',
                                 `root_id` bigint NULL DEFAULT NULL COMMENT '根节点id',
                                 `code` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
                                 `name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
                                 `type` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限类型',
                                 `urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '权限所属url',
                                 `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `tree_idx`(`parent_id` ASC, `root_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_files
-- ----------------------------
DROP TABLE IF EXISTS `im_files`;
CREATE TABLE `im_files`  (
                             `id` bigint NOT NULL,
                             `upload_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传id',
                             `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件原始名称',
                             `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型',
                             `total_size` double NULL DEFAULT NULL COMMENT '文件总大小',
                             `total_chunk` int NULL DEFAULT NULL,
                             `chunk_size` int NULL DEFAULT NULL,
                             `uploaded_chunk_count` int NULL DEFAULT NULL,
                             `etag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'minio文件标识',
                             `access_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问url',
                             `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'PENDING, COMPLETED, FAILED',
                             `storage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                             `create_time` datetime NULL DEFAULT NULL,
                             `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `upload_id_idx`(`upload_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group`  (
                             `id` bigint NOT NULL,
                             `name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群名称',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群组头像',
                             `create_user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `state` tinyint(1) NULL DEFAULT NULL COMMENT '群组状态 0  已解散 1 正常',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_group_user
-- ----------------------------
DROP TABLE IF EXISTS `im_group_user`;
CREATE TABLE `im_group_user`  (
                                  `id` bigint NOT NULL,
                                  `group_id` bigint NULL DEFAULT NULL COMMENT '关联群组id',
                                  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户id',
                                  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `group_id_idx`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_msg`;
CREATE TABLE `im_msg`  (
                           `id` bigint NOT NULL COMMENT '主键',
                           `sender_user_id` bigint NULL DEFAULT NULL COMMENT '关联发送人用户id',
                           `receiver_user_id` bigint NULL DEFAULT NULL COMMENT '关联接收人用户id',
                           `type` int NULL DEFAULT NULL COMMENT '消息类型',
                           `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
                           `content_metadata` json NULL COMMENT '消息内容元信息',
                           `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
                           `delivery_method` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投递方式',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_offline_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_offline_msg`;
CREATE TABLE `im_offline_msg`  (
                                   `id` bigint NOT NULL,
                                   `msg_id` bigint NULL DEFAULT NULL COMMENT '关联消息id',
                                   `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户id',
                                   `state` tinyint(1) NULL DEFAULT NULL COMMENT '消息状态 0 待推送 1 已推送',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_role
-- ----------------------------
DROP TABLE IF EXISTS `im_role`;
CREATE TABLE `im_role`  (
                            `id` bigint NOT NULL,
                            `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
                            `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `im_role_authority`;
CREATE TABLE `im_role_authority`  (
                                      `id` bigint NOT NULL,
                                      `role_id` bigint NOT NULL COMMENT '关联角色id',
                                      `authority_id` bigint NOT NULL COMMENT '关联权限id',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_session
-- ----------------------------
DROP TABLE IF EXISTS `im_session`;
CREATE TABLE `im_session`  (
                               `id` bigint NOT NULL,
                               `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
                               `receiver_user_id` bigint NULL DEFAULT NULL COMMENT '接收人id',
                               `delivery_method` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投递方式',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `unique_idx`(`user_id` ASC, `receiver_user_id` ASC) USING BTREE,
                               INDEX `user_id_idx`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user`  (
                            `id` bigint NOT NULL,
                            `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录账号',
                            `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                            `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                            `nickname_pinyin` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称拼音',
                            `tagline` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个性签名',
                            `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                            `phone` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
                            `gender` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
                            `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
                            `region` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `username_idx`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_user_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_user_friend`;
CREATE TABLE `im_user_friend`  (
                                   `id` bigint NOT NULL,
                                   `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户id',
                                   `friend_id` bigint NULL DEFAULT NULL COMMENT '关联好友id',
                                   `note` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                   `note_pinyin` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注拼音',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `user_id_idx`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for im_user_msg_box
-- ----------------------------
DROP TABLE IF EXISTS `im_user_msg_box`;
CREATE TABLE `im_user_msg_box`  (
                                    `id` bigint NOT NULL,
                                    `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
                                    `session_id` bigint NULL DEFAULT NULL COMMENT '会话id',
                                    `msg_id` bigint NULL DEFAULT NULL COMMENT '消息id',
                                    `box_type` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'OUTBOX 发件箱 INBOX 收件箱',
                                    `time` datetime NULL DEFAULT NULL COMMENT '消息时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `user_session_idx`(`user_id` ASC, `session_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_user_role
-- ----------------------------
DROP TABLE IF EXISTS `im_user_role`;
CREATE TABLE `im_user_role`  (
                                 `id` bigint NOT NULL,
                                 `user_id` bigint NOT NULL COMMENT '关联用户id',
                                 `role_id` bigint NOT NULL COMMENT '关联角色id',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `user_id_idx`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization`  (
                                         `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `registered_client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `principal_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `authorization_grant_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                         `authorized_scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                         `attributes` blob NULL,
                                         `state` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                         `authorization_code_value` blob NULL,
                                         `authorization_code_issued_at` timestamp NULL DEFAULT NULL,
                                         `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
                                         `authorization_code_metadata` blob NULL,
                                         `access_token_value` blob NULL,
                                         `access_token_issued_at` timestamp NULL DEFAULT NULL,
                                         `access_token_expires_at` timestamp NULL DEFAULT NULL,
                                         `access_token_metadata` blob NULL,
                                         `access_token_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                         `access_token_scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                         `oidc_id_token_value` blob NULL,
                                         `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL,
                                         `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL,
                                         `oidc_id_token_metadata` blob NULL,
                                         `refresh_token_value` blob NULL,
                                         `refresh_token_issued_at` timestamp NULL DEFAULT NULL,
                                         `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
                                         `refresh_token_metadata` blob NULL,
                                         `user_code_value` blob NULL,
                                         `user_code_issued_at` timestamp NULL DEFAULT NULL,
                                         `user_code_expires_at` timestamp NULL DEFAULT NULL,
                                         `user_code_metadata` blob NULL,
                                         `device_code_value` blob NULL,
                                         `device_code_issued_at` timestamp NULL DEFAULT NULL,
                                         `device_code_expires_at` timestamp NULL DEFAULT NULL,
                                         `device_code_metadata` blob NULL,
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for oauth2_authorization_consent
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE `oauth2_authorization_consent`  (
                                                 `registered_client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                 `principal_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                 `authorities` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                                 PRIMARY KEY (`registered_client_id`, `principal_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for oauth2_registered_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client`  (
                                             `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `client_id_issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             `client_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                             `client_secret_expires_at` timestamp NULL DEFAULT NULL,
                                             `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `client_authentication_methods` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `authorization_grant_types` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                             `post_logout_redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                                             `scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `client_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             `token_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for one_time_tokens
-- ----------------------------
DROP TABLE IF EXISTS `one_time_tokens`;
CREATE TABLE `one_time_tokens`  (
                                    `token_value` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                    `expires_at` timestamp NOT NULL,
                                    PRIMARY KEY (`token_value`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
