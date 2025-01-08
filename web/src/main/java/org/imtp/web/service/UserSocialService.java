package org.imtp.web.service;

import com.github.pagehelper.PageInfo;
import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.domain.dto.UserSessionDTO;

import java.util.List;

public interface UserSocialService {

    List<UserSessionInfo> findSessionByUserId(String userId);

    Long createUserSessionByUserId(String userId,UserSessionDTO userSessionDTO);

    Boolean deleteSessionById(String userId);

    List<UserFriendInfo> findUserFriendByUserId(String userId);

    List<UserGroupInfo> findUserGroupByUserId(String userId);

    List<OfflineMessageInfo> findOfflineMessageByUserId(String userId);

    List<String> findUserIdByGroupId(String groupId);

    Long saveMessage(MessageDTO messageDTO);

    PageInfo<MessageInfo> findMessages(String userId,String sessionId,Integer pageNum,Integer pageSize);

    //保存离线消息
    Boolean saveOfflineMessage(List<OfflineMessageDTO> offlineMessageList);

}
