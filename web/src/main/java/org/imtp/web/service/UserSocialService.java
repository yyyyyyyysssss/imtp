package org.imtp.web.service;

import com.github.pagehelper.PageInfo;
import org.imtp.common.packet.body.MessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.web.domain.dto.UserSessionDTO;

import java.util.List;

public interface UserSocialService {

    List<UserSessionInfo> findSessionByUserId(String userId);

    Long createUserSessionByUserId(String userId,UserSessionDTO userSessionDTO);

    Boolean deleteSessionById(String userId);

    List<UserFriendInfo> findUserFriendByUserId(String userId);

    List<UserGroupInfo> findUserGroupByUserId(String userId);

    List<String> findUserIdByGroupId(String groupId);

    Long saveMessage(MessageDTO messageDTO);

    PageInfo<MessageInfo> findMessages(String userId,String sessionId,String prevMsgId,Integer pageNum,Integer pageSize);

}
