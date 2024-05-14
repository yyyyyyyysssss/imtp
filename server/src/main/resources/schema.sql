SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for im_group
-- ----------------------------
DROP TABLE IF EXISTS `im_group`;
CREATE TABLE `im_group`  (
                             `id` bigint(0) NOT NULL,
                             `name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群名称',
                             `create_user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建人id',
                             `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                             `state` tinyint(1) NULL DEFAULT NULL COMMENT '群组状态 0  已解散 1 正常',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_group_user
-- ----------------------------
DROP TABLE IF EXISTS `im_group_user`;
CREATE TABLE `im_group_user`  (
                                  `id` bigint(0) NOT NULL,
                                  `group_id` bigint(0) NULL DEFAULT NULL COMMENT '关联群组id',
                                  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联用户id',
                                  `join_time` datetime(0) NULL DEFAULT NULL COMMENT '加入时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_msg`;
CREATE TABLE `im_msg`  (
                           `id` bigint(0) NOT NULL COMMENT '主键',
                           `sender_user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联发送人用户id',
                           `receiver_user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联接收人用户id',
                           `type` int(0) NULL DEFAULT NULL COMMENT '消息类型',
                           `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
                           `send_time` datetime(0) NULL DEFAULT NULL COMMENT '发送时间',
                            `delivery_method` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投递方式',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_offline_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_offline_msg`;
CREATE TABLE `im_offline_msg`  (
                                   `id` bigint(0) NOT NULL,
                                   `msg_id` bigint(0) NULL DEFAULT NULL COMMENT '关联消息id',
                                   `user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联用户id',
                                   `state` tinyint(1) NULL DEFAULT NULL COMMENT '消息状态 0 待推送 1 已推送',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user`  (
                            `id` bigint(0) NOT NULL,
                            `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录账号',
                            `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                            `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                            `gender` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
                            `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
                            `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_user_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_user_friend`;
CREATE TABLE `im_user_friend`  (
                                   `id` bigint(0) NOT NULL,
                                   `user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联用户id',
                                   `friend_id` bigint(0) NULL DEFAULT NULL COMMENT '关联好友id',
                                   `state` tinyint(1) NULL DEFAULT NULL COMMENT '好友状态 0 删除 1正常',
                                   `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `user_id_idx`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for im_user_session
-- ----------------------------
DROP TABLE IF EXISTS `im_user_session`;
CREATE TABLE `im_user_session`  (
                                    `id` bigint NOT NULL,
                                    `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户id',
                                    `receiver_id` bigint NULL DEFAULT NULL COMMENT '接收人id',
                                    `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


SET FOREIGN_KEY_CHECKS = 1;
