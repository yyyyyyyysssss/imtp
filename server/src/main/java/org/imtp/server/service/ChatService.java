package org.imtp.server.service;

import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.entity.User;

import java.util.List;

public interface ChatService {

    //查询用户
    User findByUsername(String username);

    //根据用户查询关联的好友
    List<User> findFriendByUserId(Long userId);

    //根据群组id查询用户
    List<User> findUserByGroupId(Long groupId);

    boolean saveOfflineMessage(OfflineMessage offlineMessage);

    List<OfflineMessage> findOfflineMessageByUserId(Long userId);

}
