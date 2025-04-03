package org.imtp.server.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.convert.MappingRedisConverter;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import java.time.Duration;
import java.util.Collections;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 17:52
 */
@Configuration
@Slf4j
public class RedisMQConfig {


    @Bean
    public StreamListener<String, ObjectRecord<String,ForwardMessage>> streamListener(){

        return new ChatMessageStreamListener();
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String,ForwardMessage>> streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory){
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String,ForwardMessage>> containerOptions = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(ForwardMessage.class)
                .build();
        //创建消除监听容器
        return StreamMessageListenerContainer
                .create(redisConnectionFactory, containerOptions);
    }

    //基于流的轻量级消息队列
    @Bean
    public Subscription subscription(StreamMessageListenerContainer<String, ObjectRecord<String,ForwardMessage>> streamMessageListenerContainer) {

        //主题消息消费配置
        StreamOffset<String> streamOffset = StreamOffset.create("message_forward2", ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamReadRequest<String> readRequest = StreamMessageListenerContainer.StreamReadRequest.builder(streamOffset)
                .cancelOnError((err) -> false)  // 异常后不停止消费
                .errorHandler((err) -> log.error(err.getMessage())) //输出异常信息
                .build();
        //消息订阅
        return streamMessageListenerContainer.register(readRequest, streamListener());
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
