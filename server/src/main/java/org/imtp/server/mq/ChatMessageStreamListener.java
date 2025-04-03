package org.imtp.server.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 17:48
 */
@Slf4j
public class ChatMessageStreamListener implements StreamListener<String, ObjectRecord<String,ForwardMessage>> {

    @Override
    public void onMessage(ObjectRecord<String, ForwardMessage> message) {
        ForwardMessage forwardMessage = message.getValue();
        log.info("ForwardMessage: {}",forwardMessage);
    }
}
