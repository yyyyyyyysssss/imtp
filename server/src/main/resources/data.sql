-- 初始用户
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (1, '147', '123456', '大鱼', '男', NULL, '2024-04-28 18:02:18');
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (2, '258', '123456', '小鱼', '女', NULL, '2024-04-28 18:02:44');
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (3, '369', '123456', '!', '男', NULL, '2024-04-28 18:03:04');

-- 初始群组
INSERT INTO `im_group`(`id`, `name`, `create_user_id`, `create_time`, `state`) VALUES (1, '测试群', 3, '2024-04-30 13:07:16', 1);

-- 初始好友关系
INSERT INTO `im_user_friend` VALUES (1, 1, 2, 1, '2024-04-30 13:05:21');
INSERT INTO `im_user_friend` VALUES (2, 1, 3, 1, '2024-04-30 13:05:34');
INSERT INTO `im_user_friend` VALUES (3, 2, 1, 1, '2024-04-30 13:05:58');
INSERT INTO `im_user_friend` VALUES (4, 2, 3, 1, '2024-04-30 13:06:22');
INSERT INTO `im_user_friend` VALUES (5, 3, 1, 1, '2024-04-30 13:06:42');

-- 初始化群好友
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (1, 1, 1, '2024-04-30 13:07:35');
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (2, 1, 2, '2024-04-30 13:07:43');
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (3, 1, 3, '2024-04-30 13:07:51');


-- 初始会话,用于测试
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (1, 1, 1977600293847040, 2, 'SINGLE');
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (2, 1, 2875645314138112, 3, 'SINGLE');
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (3, 1, 1971606382272512, 1, 'GROUP');
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (4, 1, 1971606382272512, 1, 'SINGLE');

