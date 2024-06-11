-- 初始用户
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (1, '147', '123456', '卡卡罗特', 'MALE', 'classpath:/img/卡卡罗特.jpg', '2024-04-28 18:02:18');
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (2, '258', '123456', '贝吉塔', 'MALE', 'classpath:/img/贝吉塔.jpg', '2024-04-28 18:02:44');
INSERT INTO `im_user`(`id`, `username`, `password`, `nickname`, `gender`, `avatar`, `create_time`) VALUES (3, '369', '123456', '孙悟饭', 'MALE', 'classpath:/img/孙悟饭.jpg', '2024-04-28 18:03:04');

-- 初始群组
INSERT INTO `im_group`(`id`, `name`, `avatar`, `create_user_id`, `create_time`, `state`) VALUES (100, '测试群', 'classpath:/img/tmp.jpg', 3, '2024-04-30 13:07:16', 1);

-- 初始好友关系
INSERT INTO `im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (1, 1, 2, 1, '2024-04-30 13:05:21');
INSERT INTO `im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (2, 1, 3, 1, '2024-04-30 13:05:34');
INSERT INTO `im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (3, 2, 1, 1, '2024-04-30 13:05:58');
INSERT INTO `im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (4, 2, 3, 1, '2024-04-30 13:06:22');
INSERT INTO `im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (5, 3, 1, 1, '2024-04-30 13:06:42');


-- 初始化群好友
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (1, 100, 1, '2024-04-30 13:07:35');
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (2, 100, 2, '2024-04-30 13:07:43');
INSERT INTO `im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (3, 100, 3, '2024-04-30 13:07:51');



-- 初始会话,用于测试
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (2, 1, 6873194505437184, 3, 'SINGLE');
INSERT INTO `im_user_session`(`id`, `user_id`, `last_msg_id`, `receiver_user_id`, `delivery_method`) VALUES (3, 1, 6861990772867072, 100, 'GROUP');


-- 初始化消息,用于测试
INSERT INTO `im_msg`(`id`, `sender_user_id`, `receiver_user_id`, `type`, `content`, `send_time`, `delivery_method`) VALUES (6873194505437184, 1, 1, 1, '阿斯顿', '2024-05-17 10:15:25', 'SINGLE');
INSERT INTO `im_msg`(`id`, `sender_user_id`, `receiver_user_id`, `type`, `content`, `send_time`, `delivery_method`) VALUES (6861990772867072, 2, 100, 1, '撒旦1321', '2024-05-17 09:30:54', 'GROUP');

