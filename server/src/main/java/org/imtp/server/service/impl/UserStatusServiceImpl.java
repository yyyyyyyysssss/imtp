package org.imtp.server.service.impl;

import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.server.config.RedisKey;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.service.UserStatusService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/25 12:37
 */
@Slf4j
@Service
public class UserStatusServiceImpl implements UserStatusService {

    @Resource
    private RedisWrapper redisWrapper;

    @Override
    public void userOnline(String userId, String channelId) {
        String k = RedisKey.USER_ONLINE + userId;
        redisWrapper.addSet(k,channelId);
    }

    @Override
    public void userOffline(String userId,String channelId) {
        String k = RedisKey.USER_ONLINE + userId;
        redisWrapper.removeSet(k,channelId);
    }

    @Override
    public void allUserOffline(){
        Collection<ChannelSession> allChannel = ChannelContextHolder.channelContext().getAllChannel();
        if (allChannel == null || allChannel.isEmpty()){
            return;
        }
        AttributeKey<Long> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        List<String> list = allChannel.stream().map(m -> RedisKey.USER_ONLINE + (m.channel().attr(attributeKey).get())).toList();
        redisWrapper.delete(list.toArray(new String[0]));
    }

    @Override
    public Map<String,Set<String>> fetchActiveChannelIdByUserIds(List<String> userIds) {
        List<String> keys = userIds.stream().map(m -> RedisKey.USER_ONLINE + m).toList();
        List<Object> set = redisWrapper.getSet(keys);
        Map<String,Set<String>> map = new HashMap<>();
        for (int i = 0; i< keys.size(); i++){
            Set<String> object = (Set<String>)set.get(i);
            String userId = userIds.get(i);
            if(object == null || object.isEmpty()){
                map.put(userId,null);
            }else {
                map.put(userId,object);
            }
        }
        return map;
    }
}
