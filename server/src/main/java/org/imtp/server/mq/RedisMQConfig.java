package org.imtp.server.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 17:52
 */
@Configuration
public class RedisMQConfig {

    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory,StreamListener<String, MapRecord<String, String, String>> chatMessageStreamListener) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);
        container.start();
        return container.receive(StreamOffset.fromStart("my-stream"),chatMessageStreamListener);
    }

}
