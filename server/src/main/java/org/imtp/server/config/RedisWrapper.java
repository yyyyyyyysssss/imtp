package org.imtp.server.config;

import jakarta.annotation.Resource;
import org.imtp.server.mq.Topic;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
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


    public <T> Long publishMsg(T message){

        return redisTemplate.convertAndSend(Topic.MESSAGE_FORWARD,message);
    }

    public <T> Long publishMsg(String topic,T message){

        return redisTemplate.convertAndSend(topic,message);
    }

    public void setValue(String key, Object object){
        redisTemplate.opsForValue().set(key,object);
    }

    public void addSet(String key,Object... values){
        redisTemplate.opsForSet().add(key,values);
    }

    public Set<Object> getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    public List<Object> getSet(Collection<String> keys){
        StringRedisSerializer keySerializer = (StringRedisSerializer) redisTemplate.getKeySerializer();
        return redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : keys) {
                connection.setCommands().sMembers(Objects.requireNonNull(keySerializer.serialize(key)));
            }
            return null;
        });
    }

    public void removeSet(String key,Object... values){
        redisTemplate.opsForSet().remove(key,values);
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
