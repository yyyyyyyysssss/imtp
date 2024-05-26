package org.imtp.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.server.entity.*;
import org.imtp.server.mapper.*;
import org.imtp.server.service.ChatService;
import org.imtp.server.service.OfflineMessageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:37
 */
@Slf4j
@Service
public class DefaultChatService implements ChatService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserFriendMapper userFriendMapper;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private GroupUserMapper groupUserMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private OfflineMessageMapper offlineMessageMapper;

    @Resource
    private OfflineMessageService offlineMessageService;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Override
    public User findByUsername(String username) {
        Wrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public List<User> findFriendByUserId(Long userId) {
        Wrapper<UserFriend> userFriendQueryWrapper = new QueryWrapper<UserFriend>().eq("user_id", userId);
        List<UserFriend> userFriends = userFriendMapper.selectList(userFriendQueryWrapper);
        if (userFriends.isEmpty()){
            return List.of();
        }
        List<Long> friendIds = userFriends.stream().map(UserFriend::getFriendId).toList();
        Wrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .select("id","nickname","username", "gender","avatar")
                .in("id", friendIds);
        return userMapper.selectList(userQueryWrapper);
    }

    @Override
    public List<Long> findUserIdByGroupId(Long groupId) {
        Wrapper<GroupUser> groupQueryWrapper = new QueryWrapper<GroupUser>()
                .select("user_id").eq("group_id", groupId);
        List<GroupUser> groupUsers = groupUserMapper.selectList(groupQueryWrapper);
        if(groupUsers.isEmpty()){
            return List.of();
        }
        List<Long> userIds = groupUsers.stream().map(GroupUser::getUserId).toList();
        Wrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .select("id").in("id", userIds);
        List<User> users = userMapper.selectList(userQueryWrapper);
        return users.isEmpty() ? List.of() : users.stream().map(User::getId).toList();
    }

    @Override
    public List<Group> findGroupByUserId(Long userId) {
        Wrapper<GroupUser> groupUserQueryWrapper = new QueryWrapper<GroupUser>()
                .eq("user_id", userId);
        List<GroupUser> groupUsers = groupUserMapper.selectList(groupUserQueryWrapper);
        if(groupUsers.isEmpty()){
            return List.of();
        }
        List<Long> groupIds = groupUsers.stream().map(GroupUser::getGroupId).toList();
        Wrapper<Group> groupQueryWrapper = new QueryWrapper<Group>()
                .eq("state",true)
                .in("id", groupIds);
        return groupMapper.selectList(groupQueryWrapper);
    }

    @Override
    public boolean saveMessage(Message message) {
        return messageMapper.insert(message) > 0;
    }

    @Override
    public boolean saveOfflineMessage(List<OfflineMessage> offlineMessages) {
        return offlineMessageService.saveBatch(offlineMessages);
    }

    @Override
    public List<Message> findOfflineMessageByUserId(Long userId) {
        Wrapper<OfflineMessage> offlineMessageQueryWrapper = new QueryWrapper<OfflineMessage>()
                .eq("state",false)
                .eq("user_id", userId);
        List<OfflineMessage> offlineMessages = offlineMessageMapper.selectList(offlineMessageQueryWrapper);
        if(offlineMessages.isEmpty()){
            return List.of();
        }
        List<Long> msgIds = offlineMessages.stream().map(OfflineMessage::getMsgId).toList();
        Wrapper<Message> messageQueryWrapper = new QueryWrapper<Message>().in("id", msgIds);
        return messageMapper.selectList(messageQueryWrapper);
    }

    @Override
    public List<UserSessionInfo> findUserSessionByUserId(Long userId) {
        Wrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<UserSession>()
                .in("user_id", userId);
        List<UserSession> userSessions = userSessionMapper.selectList(userSessionQueryWrapper);
        if (userSessions.isEmpty()){
            return List.of();
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> msgIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();
        for (UserSession userSession : userSessions){
            if(userSession.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
                userIds.add(userSession.getReceiverUserId());
            }else {
                groupIds.add(userSession.getReceiverUserId());
            }

            msgIds.add(userSession.getLastMsgId());
        }
        Map<Long, User> userIdMap = null;
        if (!userIds.isEmpty()){
            List<User> users = userMapper.selectBatchIds(userIds);
            userIdMap = users.stream().collect(Collectors.toMap(User::getId, a -> a));
        }
        Map<Long, Group> groupMap = null;
        if (!groupIds.isEmpty()){
            List<Group> groups = groupMapper.selectBatchIds(groupIds);
            groupMap = groups.stream().collect(Collectors.toMap(Group::getId, a -> a));
        }

        List<Message> messages = messageMapper.selectBatchIds(msgIds);
        Map<Long, Message> messageIdMap = messages.stream().collect(Collectors.toMap(Message::getId, a -> a));
        List<UserSessionInfo> userSessionInfos = new ArrayList<>();
        UserSessionInfo userSessionInfo;
        for (UserSession userSession : userSessions){
            userSessionInfo = new UserSessionInfo();
            userSessionInfo.setId(userSession.getId());
            userSessionInfo.setUserId(userSession.getUserId());
            userSessionInfo.setReceiverUserId(userSession.getReceiverUserId());

            if (userSession.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
                User user = Optional.ofNullable(userIdMap).map(m -> m.get(userSession.getReceiverUserId())).orElse(new User());
                userSessionInfo.setName(user.getNickname());
                userSessionInfo.setAvatar(user.getAvatar());
            }else {
                Group group = Optional.ofNullable(groupMap).map(m -> m.get(userSession.getReceiverUserId())).orElse(new Group());
                userSessionInfo.setName(group.getName());
                userSessionInfo.setAvatar(group.getAvatar());
            }

            Message message = messageIdMap.get(userSession.getLastMsgId());
            MessageType messageType = MessageType.findMessageTypeByValue(message.getType());
            userSessionInfo.setLastMsgType(messageType);
            userSessionInfo.setLastMsgContent(message.getContent());
            userSessionInfo.setLastMsgTime(message.getSendTime().getTime());
            userSessionInfo.setDeliveryMethod(message.getDeliveryMethod());

            userSessionInfos.add(userSessionInfo);
        }

        return userSessionInfos;
    }
}
