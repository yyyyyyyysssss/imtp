package org.imtp.web.service;

import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.web.domain.dto.UserSessionDTO;

import java.util.List;

public interface UserSocialService {

    List<UserSessionInfo> userSession(String userId);

    String userSession(UserSessionDTO userSessionDTO);

    List<UserFriendInfo> userFriend(String userId);

    List<UserGroupInfo> userGroup(String userId);

}
