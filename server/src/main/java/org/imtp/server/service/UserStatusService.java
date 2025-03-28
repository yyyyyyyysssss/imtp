package org.imtp.server.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStatusService {

    //用户上线
    void userOnline(String userId,String channelId);

    //用户下线
    void userOffline(String userId, String channelId);

    //所有用户下线
    void allUserOffline();

    //获取用户已登录的所有端
    Map<String,Set<String>> fetchActiveChannelIdByUserIds(List<String> userIds);

}
