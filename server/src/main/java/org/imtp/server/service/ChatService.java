package org.imtp.server.service;

import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.server.entity.Group;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.entity.User;

import java.util.List;

public interface ChatService {

    //根据用户名查询用户
    User findByUsername(String username);

    //根据用户查询关联的好友
    List<User> findFriendByUserId(Long userId);

    //根据群组id查询关联的用户id
    List<Long> findUserIdByGroupId(Long groupId);

    //根据用户id查询关联的群组
    List<Group> findGroupByUserId(Long userId);

    //保存离线消息
    boolean saveMessage(Message message);

    //保存离线消息
    boolean saveOfflineMessage(List<OfflineMessage> offlineMessages);

    //根据用户id查询该用户的离线消息
    List<Message> findOfflineMessageByUserId(Long userId);

    //根据用户id查询会话
    List<UserSessionInfo> findUserSessionByUserId(Long userId);

}
