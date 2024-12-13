package org.imtp.web.service;

import com.github.pagehelper.PageInfo;
import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.web.domain.dto.UserSessionDTO;

import java.util.List;

public interface UserSocialService {

    List<UserSessionInfo> userSession(String userId);

    String userSession(String userId,UserSessionDTO userSessionDTO);

    Boolean userSession(String userId,String sessionId);

    List<UserFriendInfo> userFriend(String userId);

    List<UserGroupInfo> userGroup(String userId);

    List<OfflineMessageInfo> offlineMessage(String userId);

    List<String> userIds(String groupId);

    //保存离线消息
    Long message(MessageDTO messageDTO);

    PageInfo<MessageInfo> message(String userId,String sessionId,Integer pageNum,Integer pageSize);

    //保存离线消息
    Boolean offlineMessage(List<OfflineMessageDTO> offlineMessageList);

}
