package org.imtp.server.mq;

import jakarta.annotation.Resource;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.config.RedisWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/3 13:31
 */

@SpringBootTest
public class RedisMQTest {


    @Resource
    private RedisWrapper redisWrapper;

    @Test
    void testStreamListener() {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChannelIds(List.of("1234567890"));
        forwardMessage.setMessage(new TextMessage("hello world",1,2,12345678L));
        redisWrapper.addStreamRecord(forwardMessage);
    }


    @Test
    void testMessageDelegate() {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChannelIds(List.of("1234567890"));
        forwardMessage.setMessage(new TextMessage("hello world",1,2,12345678L));
        redisWrapper.publishMsg(forwardMessage);
    }

}
