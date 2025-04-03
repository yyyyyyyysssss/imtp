package org.imtp.server.mq;

import jakarta.annotation.Resource;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/3 13:57
 */
@Component
public class RedisStreamLifecycle implements SmartLifecycle {

    private boolean isRunning = false;

    @Resource
    private StreamMessageListenerContainer<String, ObjectRecord<String,ForwardMessage>> streamMessageListenerContainer;

    @Override
    public void start() {
        if (!isRunning){
            streamMessageListenerContainer.start();
            isRunning = true;
        }
    }

    @Override
    public void stop() {
        if (isRunning){
            streamMessageListenerContainer.stop();
            isRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
