package org.imtp.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.server.entity.*;
import org.imtp.server.mapper.*;
import org.imtp.server.service.ChatService;
import org.imtp.server.service.OfflineMessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public User findByUsername(String username) {
        Wrapper<User> queryWrapper = new QueryWrapper<User>().eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public List<User> findFriendByUserId(Long userId) {
        Wrapper<UserFriend> userFriendQueryWrapper = new QueryWrapper<UserFriend>().eq("userId", userId);
        List<UserFriend> userFriends = userFriendMapper.selectList(userFriendQueryWrapper);
        if (userFriends.isEmpty()){
            return List.of();
        }
        List<Long> friendIds = userFriends.stream().map(UserFriend::getFriendId).toList();
        Wrapper<User> userQueryWrapper = new QueryWrapper<User>().in("id", friendIds);
        return userMapper.selectList(userQueryWrapper);
    }

    @Override
    public List<Long> findUserIdByGroupId(Long groupId) {
        Wrapper<GroupUser> groupQueryWrapper = new QueryWrapper<GroupUser>()
                .select("userId").eq("groupId", groupId);
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
        Wrapper<GroupUser> groupUserQueryWrapper = new QueryWrapper<GroupUser>().eq("userId", userId);
        List<GroupUser> groupUsers = groupUserMapper.selectList(groupUserQueryWrapper);
        if(groupUsers.isEmpty()){
            return List.of();
        }
        List<Long> groupIds = groupUsers.stream().map(GroupUser::getGroupId).toList();
        Wrapper<Group> groupQueryWrapper = new QueryWrapper<Group>().eq("id", groupIds);
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
        Wrapper<OfflineMessage> offlineMessageQueryWrapper = new QueryWrapper<OfflineMessage>().eq("userId", userId);
        List<OfflineMessage> offlineMessages = offlineMessageMapper.selectList(offlineMessageQueryWrapper);
        if(offlineMessages.isEmpty()){
            return List.of();
        }
        List<Long> msgIds = offlineMessages.stream().map(OfflineMessage::getMsgId).toList();
        Wrapper<Message> messageQueryWrapper = new QueryWrapper<Message>().eq("id", msgIds);
        return messageMapper.selectList(messageQueryWrapper);
    }


}
