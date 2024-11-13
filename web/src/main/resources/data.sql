-- 初始用户
INSERT INTO `im`.`im_user`(`id`, `username`, `password`, `nickname`, `note`, `tagline`, `email`, `phone`, `gender`, `avatar`, `region`, `create_time`) VALUES (1, '147', '123456', '卡卡罗特', '孙悟空', '正在拯救地球', '1085385084@qq.com', NULL, 'MALE', 'http://localhost:9000/y-chat-bucket/d4b0fb7c889d466183188f286ca03446.jpg', '地球', '2024-04-28 18:02:18');
INSERT INTO `im`.`im_user`(`id`, `username`, `password`, `nickname`, `note`, `tagline`, `email`, `phone`, `gender`, `avatar`, `region`, `create_time`) VALUES (2, '258', '123456', '贝吉塔', '赛亚人王子', '', '2622540847@qq.com', NULL, 'MALE', 'http://localhost:9000/y-chat-bucket/08ddb70d687f445892a294a684ec0ba3.jpg', '贝吉塔行星', '2024-04-28 18:02:44');
INSERT INTO `im`.`im_user`(`id`, `username`, `password`, `nickname`, `note`, `tagline`, `email`, `phone`, `gender`, `avatar`, `region`, `create_time`) VALUES (3, '369', '123456', '比克大魔王', '短笛', '', 'sun@email.com', NULL, 'MALE', 'http://localhost:9000/y-chat-bucket/0e258bedd82e44a380f82817b48f3ca6.jpeg', '娜美克星', '2024-04-28 18:03:04');

-- 初始群组
INSERT INTO `im`.`im_group`(`id`, `name`, `avatar`, `create_user_id`, `create_time`, `state`) VALUES (100, '测试群', 'http://localhost:9000/y-chat-bucket/787a2edea667438ba6dca16ddcdd5398.jpg', 3, '2024-04-30 13:07:16', 1);

-- 初始好友关系
INSERT INTO `im`.`im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (1, 1, 2, 1, '2024-04-30 13:05:21');
INSERT INTO `im`.`im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (2, 1, 3, 1, '2024-04-30 13:05:34');
INSERT INTO `im`.`im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (3, 2, 1, 1, '2024-04-30 13:05:58');
INSERT INTO `im`.`im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (4, 2, 3, 1, '2024-04-30 13:06:22');
INSERT INTO `im`.`im_user_friend`(`id`, `user_id`, `friend_id`, `state`, `create_time`) VALUES (5, 3, 1, 1, '2024-04-30 13:06:42');


-- 初始化群好友
INSERT INTO `im`.`im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (1, 100, 1, '2024-04-30 13:07:35');
INSERT INTO `im`.`im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (2, 100, 2, '2024-04-30 13:07:43');
INSERT INTO `im`.`im_group_user`(`id`, `group_id`, `user_id`, `join_time`) VALUES (3, 100, 3, '2024-04-30 13:07:51');



-- 初始会话,用于测试
INSERT INTO `im`.`im_user_session`(`id`, `user_id`, `receiver_user_id`, `delivery_method`, `last_msg_id`) VALUES (3, 1, 100, 'GROUP', 6861990772867072);
INSERT INTO `im`.`im_user_session`(`id`, `user_id`, `receiver_user_id`, `delivery_method`, `last_msg_id`) VALUES (53429255697772544, 2, 1, 'SINGLE', 59161938417131520);


-- 初始化消息,用于测试
INSERT INTO `im`.`im_msg`(`id`, `sender_user_id`, `receiver_user_id`, `type`, `content`, `content_metadata`, `send_time`, `delivery_method`) VALUES (6861990772867072, 2, 100, 1, '弗利萨来了', NULL, '2024-09-05 09:30:54', 'GROUP');
INSERT INTO `im`.`im_msg`(`id`, `sender_user_id`, `receiver_user_id`, `type`, `content`, `content_metadata`, `send_time`, `delivery_method`) VALUES (59161938417131520, 1, 2, 6, 'http://localhost:9000/y-chat-bucket/9d9df5b68cee4239ab78f2303bcd34c3.xlsx', '{\"name\": \"新建 Microsoft Excel 工作表.xlsx\", \"size\": 6598, \"width\": null, \"height\": null, \"duration\": null, \"sizeDesc\": \"6.4K\", \"mediaType\": \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\", \"previewUrl\": null, \"durationDesc\": null, \"thumbnailUrl\": null}', '2024-10-08 17:12:13', 'SINGLE');

-- oauth2客户端
INSERT INTO `im`.`oauth2_registered_client`(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`) VALUES ('2102a5df-4706-4aec-a369-213b4c82d7f3', '32b00b1e89af-90d2e0e46d20ebb92f6c', '2024-07-26 17:22:39', '123456', NULL, 'oauth-test', 'client_secret_post,client_secret_basic', 'refresh_token,client_credentials,authorization_code,urn:ietf:params:oauth:grant-type:device_code', 'http://localhost:3000/login', 'http://127.0.0.1:9090/oauth/oidc/logout', 'openid,profile,email,phone,userInfo', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",86400.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",604800.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}');

