package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.domain.dto.UserSessionDTO;
import org.imtp.web.domain.entity.*;
import org.imtp.web.mapper.*;
import org.imtp.web.service.OfflineMessageService;
import org.imtp.web.service.UserSocialService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/4 11:08
 */
@Service
public class UserSocialServiceImpl implements UserSocialService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

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
    public List<UserSessionInfo> userSession(String userId) {
        Wrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<UserSession>()
                .in("user_id", userId)
                .orderByDesc("id");

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
            if(userSession.getLastMsgId() != null){
                msgIds.add(userSession.getLastMsgId());
            }
        }
        //好友
        Map<Long, User> userIdMap = null;
        if (!userIds.isEmpty()){
            List<User> users = userMapper.selectBatchIds(userIds);
            userIdMap = users.stream().collect(Collectors.toMap(User::getId, a -> a));
        }
        //群组
        Map<Long, Group> groupMap = null;
        if (!groupIds.isEmpty()){
            List<Group> groups = groupMapper.selectBatchIds(groupIds);
            groupMap = groups.stream().collect(Collectors.toMap(Group::getId, a -> a));
        }
        //最新消息
        Map<Long, Message> messageIdMap = null;
        if(!msgIds.isEmpty()){
            List<Message> messages = messageMapper.selectBatchIds(msgIds);
            messageIdMap = new HashMap<>();
            for (Message message : messages){
                userIds.add(message.getSenderUserId());
                messageIdMap.put(message.getId(),message);
            }
        }
        //返回数据
        List<UserSessionInfo> userSessionInfos = new ArrayList<>();
        UserSessionInfo userSessionInfo;
        for (UserSession userSession : userSessions){
            userSessionInfo = new UserSessionInfo();
            userSessionInfo.setId(userSession.getId());
            userSessionInfo.setUserId(userSession.getUserId());
            userSessionInfo.setReceiverUserId(userSession.getReceiverUserId());
            userSessionInfo.setDeliveryMethod(userSession.getDeliveryMethod());

            if (userSession.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
                User user = Optional.ofNullable(userIdMap).map(m -> m.get(userSession.getReceiverUserId())).orElse(null);
                if(user != null){
                    userSessionInfo.setName(user.getNickname());
                    userSessionInfo.setAvatar(user.getAvatar());
                }
            }else {
                Group group = Optional.ofNullable(groupMap).map(m -> m.get(userSession.getReceiverUserId())).orElse(null);
                if (group != null){
                    userSessionInfo.setName(group.getName());
                    userSessionInfo.setAvatar(group.getAvatar());
                }
            }

            Message message = Optional.ofNullable(messageIdMap).map(m -> m.get(userSession.getLastMsgId())).orElse(null);
            if(message != null){
                MessageType messageType = MessageType.findMessageTypeByValue(message.getType());
                userSessionInfo.setLastMsgType(messageType);
                userSessionInfo.setLastMsgContent(message.getContent());
                userSessionInfo.setLastMsgTime(message.getSendTime().getTime());
                userSessionInfo.setLastSendMsgUserId(message.getSenderUserId());
                userSessionInfo.setLastMessageMetadata(message.getContentMetadata());
                User user = Optional.ofNullable(userIdMap).map(m -> m.get(message.getSenderUserId())).orElse(null);
                if(user != null){
                    userSessionInfo.setLastUserAvatar(user.getAvatar());
                    userSessionInfo.setLastUserName(user.getNickname());
                }
            }
            userSessionInfos.add(userSessionInfo);
        }

        return userSessionInfos;
    }

    @Override
    public String userSession(UserSessionDTO userSessionDTO) {
        UserSession userSession = UserSession
                .builder()
                .id(IdGen.genId())
                .userId(Long.parseLong(userSessionDTO.getSenderUserId()))
                .receiverUserId(Long.parseLong(userSessionDTO.getReceiverUserId()))
                .lastMsgId(userSessionDTO.getLastMsgId() != null ? Long.parseLong(userSessionDTO.getLastMsgId()) : null)
                .deliveryMethod(userSessionDTO.getDeliveryMethod())
                .build();
        userSessionMapper.insert(userSession);
        return userSession.getId().toString();
    }

    @Override
    public List<UserFriendInfo> userFriend(String userId) {
        Wrapper<UserFriend> userFriendQueryWrapper = new QueryWrapper<UserFriend>().eq("user_id", userId);
        List<UserFriend> userFriends = userFriendMapper.selectList(userFriendQueryWrapper);
        if (userFriends.isEmpty()){
            return List.of();
        }
        List<Long> friendIds = userFriends.stream().map(UserFriend::getFriendId).collect(Collectors.toList());
        friendIds.add(Long.parseLong(userId));
        Wrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .select("id","nickname","username", "gender","avatar","note","tagline","region")
                .in("id", friendIds);
        List<User> users = userMapper.selectList(userQueryWrapper);
        List<UserFriendInfo> userFriendInfos = new ArrayList<>();
        for (User user : users){
            UserFriendInfo userFriendInfo = UserFriendInfo.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .note(user.getNote())
                    .tagline(user.getTagline())
                    .account(user.getUsername())
                    .gender(user.getGender())
                    .avatar(user.getAvatar())
                    .region(user.getRegion())
                    .build();
            userFriendInfos.add(userFriendInfo);
        }
        return userFriendInfos;
    }

    @Override
    public List<UserGroupInfo> userGroup(String userId) {
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
        List<Group> groups = groupMapper.selectList(groupQueryWrapper);
        List<Long> ids = groups.stream().map(Group::getId).toList();
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<GroupUser>()
                .select("user_id","group_id")
                .in("group_id", ids);
        List<GroupUser> gus = groupUserMapper.selectList(queryWrapper);
        List<Long> userIds = gus.stream().map(GroupUser::getUserId).distinct().toList();
        Wrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .select("id","nickname","avatar").in("id", userIds);
        List<User> users = userMapper.selectList(userQueryWrapper);


        Map<Long, List<GroupUser>> groupUserMap = gus.stream().collect(Collectors.groupingBy(GroupUser::getGroupId));
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, a -> a));

        List<UserGroupInfo> userGroupInfos = new ArrayList<>();
        for (Group group : groups){
            Long id = group.getId();
            UserGroupInfo groupInfo = UserGroupInfo.builder().id(id).groupName(group.getName()).avatar(group.getAvatar()).build();
            List<GroupUser> guList = groupUserMap.get(id);
            List<UserFriendInfo> groupUserInfos = new ArrayList<>();
            for (GroupUser groupUser : guList){
                User user = userMap.get(groupUser.getUserId());
                UserFriendInfo userFriendInfo = UserFriendInfo.builder().id(user.getId()).nickname(user.getNickname()).account(user.getUsername()).gender(user.getGender()).avatar(user.getAvatar()).build();
                groupUserInfos.add(userFriendInfo);
            }
            groupInfo.setGroupUserInfos(groupUserInfos);
            userGroupInfos.add(groupInfo);
        }
        return userGroupInfos;
    }

    @Override
    public List<OfflineMessageInfo> offlineMessage(String userId) {
        Wrapper<OfflineMessage> offlineMessageQueryWrapper = new QueryWrapper<OfflineMessage>()
                .eq("state",false)
                .eq("user_id", userId);
        List<OfflineMessage> offlineMessages = offlineMessageMapper.selectList(offlineMessageQueryWrapper);
        if(offlineMessages.isEmpty()){
            return List.of();
        }
        List<Long> msgIds = offlineMessages.stream().map(OfflineMessage::getMsgId).toList();
        Wrapper<Message> messageQueryWrapper = new QueryWrapper<Message>().in("id", msgIds);
        List<Message> messages = messageMapper.selectList(messageQueryWrapper);
        if (messages.isEmpty()){
            return List.of();
        }
        List<OfflineMessageInfo> offlineMessageInfos = new ArrayList<>();
        for (Message message : messages){
            OfflineMessageInfo offlineMessageInfo = OfflineMessageInfo
                    .builder()
                    .id(message.getId())
                    .sender(message.getSenderUserId())
                    .receiver(message.getReceiverUserId())
                    .type(message.getType())
                    .content(message.getContent())
                    .sendTime(message.getSendTime().getTime())
                    .deliveryMethod(message.getDeliveryMethod())
                    .build();
            offlineMessageInfos.add(offlineMessageInfo);
        }
        return offlineMessageInfos;
    }

    @Override
    public List<String> userIds(String groupId) {
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
        return users.isEmpty() ? List.of() : users.stream().map(m -> m.getId().toString()).toList();
    }

    @Override
    public Long message(MessageDTO messageDTO) {
        Message message = new Message();
        message.setId(IdGen.genId());
        message.setSessionId(messageDTO.getSessionId());
        message.setSenderUserId(messageDTO.getSenderUserId());
        message.setReceiverUserId(messageDTO.getReceiverUserId());
        message.setType(messageDTO.getType());
        message.setContent(messageDTO.getContent());
        message.setContentMetadata(messageDTO.getContentMetadata());
        message.setSendTime(new Date());
        message.setDeliveryMethod(messageDTO.getDeliveryMethod());
        int insert = messageMapper.insert(message);
        if (insert > 0){
            return message.getId();
        }
        throw new RuntimeException("insert error");
    }

    public PageInfo<MessageInfo> message(String sessionId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id",sessionId);
        queryWrapper.orderByDesc("id");
        List<MessageInfo> messageInfos = messageMapper.findMessageBySessionId(sessionId);
        if (messageInfos == null || messageInfos.isEmpty()){
            return PageInfo.of(new ArrayList<>());
        }
        return PageInfo.of(messageInfos);
    }

    @Override
    public Boolean offlineMessage(List<OfflineMessageDTO> offlineMessageList) {
        List<OfflineMessage> offlineMessages = offlineMessageList.stream().map(OfflineMessage::new).toList();
        return offlineMessageService.saveBatch(offlineMessages);
    }

}
