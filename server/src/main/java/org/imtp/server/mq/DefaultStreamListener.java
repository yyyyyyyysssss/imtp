package org.imtp.server.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 17:48
 */
@Component
@Slf4j
public class DefaultStreamListener implements StreamListener<String, MapRecord<String,String,String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        log.info("MessageId: {}, Stream: {}, Body: {}",message.getId(),message.getStream(),message.getValue());
    }
}
