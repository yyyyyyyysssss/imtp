package org.imtp.server.service;

import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChatService {

    //根据用户名查询用户
    User findByUsername(String username);

    //根据用户查询关联的好友
    List<UserFriendInfo> findFriendByUserId(Long userId);

    //根据群组id查询关联的用户id
    List<String> findUserIdByGroupId(Long groupId);

    //根据用户id查询关联的群组
    List<UserGroupInfo> findGroupByUserId(Long userId);

    //保存离线消息
    boolean saveMessage(Message message);

    //保存离线消息
    boolean saveOfflineMessage(List<OfflineMessage> offlineMessages);

    //根据用户id查询该用户的离线消息
    List<Message> findOfflineMessageByUserId(Long userId);

    //根据用户id查询会话
    List<UserSessionInfo> findUserSessionByUserId(Long userId);

    //用户上线
    void userOnline(String userId,String channelId);

    //用户下线
    void userOffline(String userId, String channelId);

    //所有用户下线
    void allUserOffline();

    //获取用户已登录的所有端
    Map<String,Set<String>> fetchActiveChannelIdByUserIds(List<String> userIds);

    //批量获取用户上线状态
    List<String> batchGetUserOnline(Collection<String> userIds);

}
