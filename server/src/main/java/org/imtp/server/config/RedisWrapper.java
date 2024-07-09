package org.imtp.server.config;

import jakarta.annotation.Resource;
import org.imtp.server.mq.Topic;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/9 16:13
 */
@Component
public class RedisWrapper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    public Long publishMsg(Object message){

        return redisTemplate.convertAndSend(Topic.MESSAGE_FORWARD,message);
    }

    public Long publishMsg(String topic,Object message){

        return redisTemplate.convertAndSend(topic,message);
    }

    public void userOnline(String userId){
        String k = RedisKey.USER_ONLINE + userId;
        setValue(k,userId);
    }

    public void userOffline(String userId){
        String k = RedisKey.USER_ONLINE + userId;
        delete(k);
    }

    public List<Object> getUserOnline(Collection<String> userIds){
        List<String> keys = userIds.stream().map(m -> RedisKey.USER_ONLINE + userIds).collect(Collectors.toList());
        return getMultiValue(keys);
    }

    public void setValue(String key, Object object){
        redisTemplate.opsForValue().set(key,object);
    }

    public void setValue(String key, Object object, Duration duration){
        redisTemplate.opsForValue().set(key,object,duration);
    }

    public Object getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public List<Object> getMultiValue(Collection<String> keys){

        return redisTemplate.opsForValue().multiGet(keys);
    }


    public boolean delete(String... keys){

        return Optional.ofNullable(redisTemplate.delete(Arrays.asList(keys))).orElse(-1L) == keys.length;
    }

}
