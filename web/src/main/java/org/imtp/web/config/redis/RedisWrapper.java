package org.imtp.web.config.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/9 16:13
 */
@Component
public class RedisWrapper {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public void setValue(String key, Object object){
        redisTemplate.opsForValue().set(key,object);
    }

    public void setValue(String key, Object object, Duration duration){
        redisTemplate.opsForValue().set(key,object,duration);
    }

    public boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public List<Object> getMultiValue(Collection<String> keys){

        return redisTemplate.opsForValue().multiGet(keys);
    }

    public Long addSet(String key,Object... objects){
        return redisTemplate.opsForSet().add(key, objects);
    }

    public Long removeSet(String key,Object... objects){
        return redisTemplate.opsForSet().remove(key, objects);
    }

    public Set<Object> getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean addZSet(String key,Object value,double score,Duration duration){
        Boolean b = redisTemplate.opsForZSet().add(key, value, score);
        redisTemplate.expire(key,duration);
        return b;
    }

    public Boolean setZSetScore(String key,Object value,double score){
        return redisTemplate.opsForZSet().add(key,value,score);
    }

    public Set<Object> rangeAllZSet(String key){

        return redisTemplate.opsForZSet().range(key,0,-1);
    }

    public Set<Object> rangeByScoreZSet(String key,double min, double max){

        return redisTemplate.opsForZSet().rangeByScore(key,min,max);
    }

    public Long removeZSet(String key,Object... values){

        return redisTemplate.opsForZSet().remove(key,values);
    }

    public boolean delete(String... keys){

        return Optional.ofNullable(redisTemplate.delete(Arrays.asList(keys))).orElse(-1L) == keys.length;
    }

}
