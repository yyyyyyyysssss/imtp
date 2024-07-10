package org.imtp.server.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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

    //基于流的轻量级消息队列
    @Bean
    public Subscription subscription(RedisConnectionFactory redisConnectionFactory,StreamListener<String, MapRecord<String, String, String>> defaultStreamListener) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);
        container.start();
        return container.receive(StreamOffset.fromStart("my-stream"),defaultStreamListener);
    }

    //基于发布订阅的轻量级消息队列
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,MessageListenerAdapter messageListenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, ChannelTopic.of(Topic.MESSAGE_FORWARD));
        return container;
    }

    @Bean
    public MessageDelegate messageDelegate(){
        return new ChatMessageDelegate();
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(MessageDelegate messageDelegate){
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(messageDelegate, "handleMessage");
        messageListenerAdapter.setSerializer(new GenericJackson2JsonRedisSerializer());
        return messageListenerAdapter;
    }

}
