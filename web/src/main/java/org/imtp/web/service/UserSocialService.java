package org.imtp.web.service;

import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.domain.dto.UserSessionDTO;
import org.imtp.web.domain.entity.Message;
import org.imtp.web.domain.entity.OfflineMessage;

import java.util.List;

public interface UserSocialService {

    List<UserSessionInfo> userSession(String userId);

    String userSession(UserSessionDTO userSessionDTO);

    List<UserFriendInfo> userFriend(String userId);

    List<UserGroupInfo> userGroup(String userId);

    List<OfflineMessageInfo> offlineMessage(String userId);

    List<String> userIds(String groupId);

    //保存离线消息
    Long message(MessageDTO messageDTO);

    //保存离线消息
    Boolean offlineMessage(List<OfflineMessageDTO> offlineMessageList);

}
