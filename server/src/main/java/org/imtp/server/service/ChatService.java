package org.imtp.server.service;

import org.imtp.server.entity.HistoryMessage;
import org.imtp.server.entity.User;

import java.util.List;

public interface ChatService {

    //查询用户
    User findByUserId(Long userId);

    //根据用户查询关联的好友
    List<User> findFriendByUserId(Long userId);

    //根据群组id查询用户
    List<User> findUserByGroupId(Long userId);

    boolean saveHistoryMessage(HistoryMessage historyMessage);

    List<HistoryMessage> findHistoryMessageByUserId(Long userId);

}
