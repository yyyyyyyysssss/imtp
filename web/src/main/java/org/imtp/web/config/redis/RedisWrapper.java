package org.imtp.web.config.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
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
        setValue(key,object,null);
    }

    public void setValue(String key, Object object, Duration duration){
        if (duration != null){
            redisTemplate.opsForValue().set(key,object,duration);
        }else {
            redisTemplate.opsForValue().set(key,object);
        }
    }

    public Long incr(String key){
        return incr(key,1);
    }

    public Long incr(String key,long delta){
        return redisTemplate.opsForValue().increment(key,delta);
    }

    public Long decr(String key){
        return decr(key,1);
    }

    public Long decr(String key,long delta){
        return redisTemplate.opsForValue().decrement(key,delta);
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

    public Boolean addZSet(String key,Object value,double score){
        return addZSet(key,value,score,null);
    }

    public Boolean addZSet(String key,Object value,double score,Duration duration){
        Boolean b = redisTemplate.opsForZSet().add(key, value, score);
        if (duration != null){
            expire(key,duration);
        }
        return b;
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

    public void addHash(String k,String field,Object value){
        addHash(k,field,value,null);
    }

    public void addHash(String k,String field,Object value,Duration duration){
        Map<String,Object> kv = new HashMap<>();
        kv.put(field,value);
        addHash(k,kv,duration);
    }

    public void addHash(String k,Map<String,Object> kv){
        addHash(k,kv,null);
    }

    public void addHash(String k,Map<String,Object> kv,Duration duration){
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(k,kv);
        if (duration != null){
            expire(k,duration);
        }
    }

    public Object getHash(String k,String field){
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(k, field);
    }

    public Map<String,Object> getHashAll(String k){
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(k);
    }

    public Long incrHash(String k,String field){
        return incrHash(k,field,1);
    }

    public Long incrHash(String k,String field,long delta){
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.increment(k,field,delta);
    }


    public Boolean delete(String... keys){

        return Optional.ofNullable(redisTemplate.delete(Arrays.asList(keys))).orElse(-1L) == keys.length;
    }

    public Boolean expire(String k,Duration duration){
        return redisTemplate.expire(k,duration);
    }

}
