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
import org.imtp.web.enums.MessageBoxType;
import org.imtp.web.mapper.*;
import org.imtp.web.service.OfflineMessageService;
import org.imtp.web.service.UserMessageBoxService;
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
    private SessionMapper sessionMapper;

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
    private UserMessageBoxMapper userMessageBoxMapper;

    @Resource
    private UserMessageBoxService userMessageBoxService;

    @Resource
    private OfflineMessageMapper offlineMessageMapper;

    @Resource
    private OfflineMessageService offlineMessageService;

    @Override
    public List<UserSessionInfo> findSessionByUserId(String userId) {
        List<Session> sessions = sessionMapper.findSessionByUserId(userId);
        if (sessions.isEmpty()){
            return List.of();
        }
        List<Long> sessionIds = sessions.stream().map(Session::getId).toList();
        List<UserSessionInfo> userSessionInfos = userSessionMapper.findUserSessionBySessionIds(sessionIds, userId);

        List<MessageInfo> latestMessageInfos = messageMapper.findLatestMessageBySessionIds(sessionIds);
        if (latestMessageInfos != null && !latestMessageInfos.isEmpty()){
            Map<Long, MessageInfo> latestMessageInfoMap = latestMessageInfos.stream().collect(Collectors.toMap(MessageInfo::getSessionId, a -> a));
            userSessionInfos = userSessionInfos.stream().peek(p -> {
                MessageInfo messageInfo = latestMessageInfoMap.get(p.getId());
                if (messageInfo != null){
                    p.setLastMsgType(MessageType.findMessageTypeByValue(messageInfo.getType()));
                    p.setLastMsgContent(messageInfo.getContent());
                    p.setLastMsgTime(messageInfo.getSendTime());
                    p.setLastUserName(messageInfo.getName());
                }
            }).toList();
        }
        return userSessionInfos;
    }

    @Override
    public String createUserSessionByUserId(String userId,UserSessionDTO userSessionDTO) {
        userSessionDTO.setSenderUserId(userId);
        DeliveryMethod deliveryMethod = userSessionDTO.getDeliveryMethod();
        QueryWrapper<Session> sessionQueryWrapper = new QueryWrapper<>();
        sessionQueryWrapper.select("id");
        if (deliveryMethod.equals(DeliveryMethod.SINGLE)){
            sessionQueryWrapper
                    .nested(n -> n.eq("sender_user_id",userSessionDTO.getSenderUserId()).eq("receiver_user_id",userSessionDTO.getReceiverUserId()))
                    .or()
                    .nested(n -> n.eq("sender_user_id",userSessionDTO.getReceiverUserId()).eq("receiver_user_id",userSessionDTO.getSenderUserId()));

        }else {
            sessionQueryWrapper
                    .eq("receiver_user_id",userSessionDTO.getReceiverUserId());
        }
        Session session = sessionMapper.selectOne(sessionQueryWrapper);
        Long sessionId;
        if (session == null){
            Session newSession = Session
                    .builder()
                    .id(IdGen.genId())
                    .senderUserId(Long.parseLong(userSessionDTO.getSenderUserId()))
                    .receiverUserId(Long.parseLong(userSessionDTO.getReceiverUserId()))
                    .deliveryMethod(userSessionDTO.getDeliveryMethod())
                    .build();
            sessionMapper.insert(newSession);
            sessionId = newSession.getId();
            UserSession userSession = UserSession
                    .builder()
                    .id(IdGen.genId())
                    .userId(Long.valueOf(userSessionDTO.getSenderUserId()))
                    .sessionId(newSession.getId())
                    .build();
            userSessionMapper.insert(userSession);
        }else {
            sessionId = session.getId();
            QueryWrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<>();
            userSessionQueryWrapper.select("id");
            userSessionQueryWrapper.eq("user_id",userSessionDTO.getSenderUserId());
            userSessionQueryWrapper.eq("session_id",session.getId());
            UserSession userSession = userSessionMapper.selectOne(userSessionQueryWrapper);
            if (userSession == null){
                UserSession newUserSession = UserSession
                        .builder()
                        .id(IdGen.genId())
                        .userId(Long.valueOf(userSessionDTO.getSenderUserId()))
                        .sessionId(session.getId())
                        .build();
                userSessionMapper.insert(newUserSession);
            }
        }
        return sessionId.toString();
    }

    @Override
    public Boolean deleteUserSessionByUserIdAndSessionId(String userId, String sessionId) {
        QueryWrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<>();
        userSessionQueryWrapper.eq("user_id",userId);
        userSessionQueryWrapper.eq("session_id",sessionId);
        int delete = userSessionMapper.delete(userSessionQueryWrapper);
        return delete > 0;
    }

    @Override
    public List<UserFriendInfo> findUserFriendByUserId(String userId) {
        return userFriendMapper.findUserFriendByUserId(userId);
    }

    @Override
    public List<UserGroupInfo> findUserGroupByUserId(String userId) {
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
    public List<OfflineMessageInfo> findOfflineMessageByUserId(String userId) {
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
    public List<String> findUserIdByGroupId(String groupId) {
        Wrapper<GroupUser> groupQueryWrapper = new QueryWrapper<GroupUser>()
                .select("user_id").eq("group_id", groupId);
        List<GroupUser> groupUsers = groupUserMapper.selectList(groupQueryWrapper);
        if(groupUsers.isEmpty()){
            return List.of();
        }
        return groupUsers.stream().map(m -> m.getUserId().toString()).toList();
    }

    @Override
    public Long saveMessage(MessageDTO messageDTO) {
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
        if (insert == 0){
            throw new RuntimeException("insert error");
        }
        UserMessageBox outboxUserMessageBox = UserMessageBox
                .builder()
                .id(IdGen.genId())
                .userId(messageDTO.getSenderUserId())
                .sessionId(message.getSessionId())
                .msgId(message.getId())
                .boxType(MessageBoxType.OUTBOX)
                .build();
        userMessageBoxMapper.insert(outboxUserMessageBox);

        //扩散写
        List<UserMessageBox> userMessageBoxes = new ArrayList<>();
        List<String> userIds = getUserIdByDeliveryMethod(messageDTO.getReceiverUserId().toString(), messageDTO.getDeliveryMethod());
        for (String userId : userIds){
            if (userId.equals(messageDTO.getSenderUserId().toString())){
                continue;
            }
            UserMessageBox inboxUserMessageBox = UserMessageBox
                    .builder()
                    .id(IdGen.genId())
                    .userId(Long.valueOf(userId))
                    .sessionId(message.getSessionId())
                    .msgId(message.getId())
                    .boxType(MessageBoxType.INBOX)
                    .build();
            userMessageBoxes.add(inboxUserMessageBox);
        }
        userMessageBoxService.saveBatch(userMessageBoxes);
        return message.getId();
    }

    private List<String> getUserIdByDeliveryMethod(String id,DeliveryMethod deliveryMethod){
        if (deliveryMethod.equals(DeliveryMethod.SINGLE)){
            return List.of(id);
        }else {
            return findUserIdByGroupId(id);
        }
    }

    public PageInfo<MessageInfo> findMessages(String userId,String sessionId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<UserMessageBox> userMessageBoxQueryWrapper = new QueryWrapper<>();
        userMessageBoxQueryWrapper.select("msg_id");
        userMessageBoxQueryWrapper.eq("user_id",userId);
        userMessageBoxQueryWrapper.eq("session_id",sessionId);
        List<UserMessageBox> userMessageBoxes = userMessageBoxMapper.selectList(userMessageBoxQueryWrapper);
        if (userMessageBoxes == null || userMessageBoxes.isEmpty()){
            return PageInfo.of(new ArrayList<>());
        }
        PageInfo<UserMessageBox> userMessageBoxPageInfo = PageInfo.of(userMessageBoxes);
        List<Long> msgIds = userMessageBoxes.stream().map(UserMessageBox::getMsgId).toList();
        List<MessageInfo> messageInfos = messageMapper.findMessageByIds(msgIds);
        if (messageInfos == null || messageInfos.isEmpty()){
            return PageInfo.of(new ArrayList<>());
        }
        messageInfos = messageInfos.stream().sorted(Comparator.comparingLong(MessageInfo::getId)).toList();
        PageInfo<MessageInfo> messageInfoPageInfo = new PageInfo<>();
        messageInfoPageInfo.setList(messageInfos);
        messageInfoPageInfo.setPageNum(pageNum);
        messageInfoPageInfo.setPageSize(pageSize);
        messageInfoPageInfo.setTotal(userMessageBoxPageInfo.getTotal());
        return messageInfoPageInfo;
    }

    @Override
    public Boolean saveOfflineMessage(List<OfflineMessageDTO> offlineMessageList) {
        List<OfflineMessage> offlineMessages = offlineMessageList.stream().map(OfflineMessage::new).toList();
        return offlineMessageService.saveBatch(offlineMessages);
    }

}
