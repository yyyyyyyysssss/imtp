package org.imtp.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.config.exception.BusinessException;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.domain.dto.UserSessionDTO;
import org.imtp.web.domain.entity.*;
import org.imtp.web.enums.MessageBoxType;
import org.imtp.web.mapper.*;
import org.imtp.web.service.OfflineMessageService;
import org.imtp.web.service.UserMessageBoxService;
import org.imtp.web.service.UserSocialService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/4 11:08
 */
@Service
@Slf4j
public class UserSocialServiceImpl implements UserSocialService {

    @Resource
    private SessionMapper sessionMapper;

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
        List<UserSessionInfo> userSessionInfos = sessionMapper.findSessionByUserId(userId);
        if (userSessionInfos.isEmpty()){
            return List.of();
        }
        List<Long> sessionIds = userSessionInfos.stream().map(UserSessionInfo::getId).toList();
        //获取每个会话关联的最新消息
        List<UserMessageBox> latestMessageIdsBySessionIds = userMessageBoxMapper.findLatestMessageIdsBySessionIds(sessionIds);
        if (latestMessageIdsBySessionIds.isEmpty()){
            return userSessionInfos;
        }
        Map<Long, Long> sessionMesageMap = latestMessageIdsBySessionIds.stream().collect(Collectors.toMap(UserMessageBox::getSessionId, UserMessageBox::getMsgId));
        List<MessageInfo> latestMessageInfos = messageMapper.findLatestMessageInfoByIds(sessionMesageMap.values());
        if(!latestMessageInfos.isEmpty()){
            Map<Long, MessageInfo> latestMessageInfoMap = latestMessageInfos.stream().collect(Collectors.toMap(MessageInfo::getId, a -> a));
            userSessionInfos = userSessionInfos.stream().peek(p -> {
                Long messageId = sessionMesageMap.get(p.getId());
                if(messageId != null){
                    MessageInfo messageInfo = latestMessageInfoMap.get(messageId);
                    if (messageInfo != null){
                        p.setLastMsgType(MessageType.findMessageTypeByValue(messageInfo.getType()));
                        p.setLastMsgContent(messageInfo.getContent());
                        p.setLastMsgTime(messageInfo.getSendTime());
                        p.setLastUserName(messageInfo.getName());
                        p.setLastMessageMetadata(messageInfo.getContentMetadata());
                    }
                }
            }).toList();
        }
        return userSessionInfos;
    }

    @Override
    public Long createUserSessionByUserId(String userId,UserSessionDTO userSessionDTO) {
        String receiverUserId = userSessionDTO.getReceiverUserId();
        QueryWrapper<Session> sessionQueryWrapper = new QueryWrapper<>();
        sessionQueryWrapper.select("id");
        sessionQueryWrapper.eq("user_id",userId);
        sessionQueryWrapper.eq("receiver_user_id",receiverUserId);
        Session session = sessionMapper.selectOne(sessionQueryWrapper);
        if (session != null){
            return session.getId();
        }
        Session insertSession = Session
                .builder()
                .id(IdGen.genId())
                .userId(Long.valueOf(userId))
                .receiverUserId(Long.valueOf(receiverUserId))
                .deliveryMethod(userSessionDTO.getDeliveryMethod())
                .build();
        try {
            int insert = sessionMapper.insert(insertSession);
            if (insert <= 0){
                throw new BusinessException("create user session fail: [" + userId + "]");
            }
        }catch (DuplicateKeyException duplicateKeyException){
            try (HintManager hintManager = HintManager.getInstance()){
                hintManager.setWriteRouteOnly();
                session = sessionMapper.selectOne(sessionQueryWrapper);
                if (session != null){
                    return session.getId();
                }else {
                    throw duplicateKeyException;
                }
            }
        }
        return insertSession.getId();
    }

    @Override
    public Boolean deleteSessionById(String sessionId) {

        return sessionMapper.deleteById(sessionId) > 0;
    }

    @Override
    public List<UserFriendInfo> findUserFriendByUserId(String userId) {
        return userFriendMapper.findUserFriendByUserId(userId);
    }

    @Override
    public List<UserGroupInfo> findUserGroupByUserId(String userId) {
        List<Group> groups = groupMapper.findGroupByUserId(userId);
        if (groups.isEmpty()){
            return List.of();
        }
        List<Long> groupIds = groups.stream().map(Group::getId).toList();
        List<GroupUserInfo> groupUserInfos = groupMapper.findGroupUserInfoByGroupIdsAndUserId(groupIds, Long.valueOf(userId));
        if (groupUserInfos.isEmpty()){
            throw new BusinessException("userId[" + userId +  "] find group exception");
        }
        Map<Long, List<GroupUserInfo>> groupIdMap = groupUserInfos.stream().collect(Collectors.groupingBy(GroupUserInfo::getGroupId));
        List<UserGroupInfo> userGroupInfos = new ArrayList<>();
        for (Group group : groups){
            Long id = group.getId();
            List<GroupUserInfo> groupUserInfoList = groupIdMap.get(id);
            UserGroupInfo userGroupInfo = UserGroupInfo
                    .builder()
                    .id(id)
                    .groupName(group.getName())
                    .note(group.getName())
                    .avatar(group.getAvatar())
                    .groupUserInfos(groupUserInfoList)
                    .build();
            userGroupInfos.add(userGroupInfo);
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
//        UserMessageBox outboxUserMessageBox = UserMessageBox
//                .builder()
//                .id(IdGen.genId())
//                .userId(messageDTO.getSenderUserId())
//                .sessionId(message.getSessionId())
//                .msgId(message.getId())
//                .boxType(MessageBoxType.OUTBOX)
//                .build();
//        userMessageBoxMapper.insert(outboxUserMessageBox);
//
//        //扩散写
//        List<UserMessageBox> userMessageBoxes = new ArrayList<>();
//        List<String> userIds = getUserIdByDeliveryMethod(messageDTO.getReceiverUserId().toString(), messageDTO.getDeliveryMethod());
//        for (String userId : userIds){
//            if (userId.equals(messageDTO.getSenderUserId().toString())){
//                continue;
//            }
//            UserMessageBox inboxUserMessageBox = UserMessageBox
//                    .builder()
//                    .id(IdGen.genId())
//                    .userId(Long.valueOf(userId))
//                    .sessionId(message.getSessionId())
//                    .msgId(message.getId())
//                    .boxType(MessageBoxType.INBOX)
//                    .build();
//            userMessageBoxes.add(inboxUserMessageBox);
//        }
//        userMessageBoxService.saveBatch(userMessageBoxes);
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
