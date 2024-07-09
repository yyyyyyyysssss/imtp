package org.imtp.server.mq;

import jakarta.annotation.Resource;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 17:48
 */
@Component
public class ChatMessageStreamListener implements StreamListener<String, MapRecord<String,String,String>> {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        System.out.println("MessageId: " + message.getId());
        System.out.println("Stream: " + message.getStream());
        System.out.println("Body: " + message.getValue());
    }
}
