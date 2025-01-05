package org.imtp.server.mq;

import jakarta.annotation.Resource;
import org.imtp.common.packet.TextMessage;
import org.imtp.server.config.RedisWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

/**
 * @Description
 * @Author ys
 * @Date 2025/1/4 11:46
 */
@SpringBootTest
public class RedisMQTest {

    @Resource
    private RedisWrapper redisWrapper;

    @Test
    public void testPublishMsg(){
        ForwardMessage forwardMessage = new ForwardMessage(Collections.emptyList(), new TextMessage("test",0,0,0L));
        redisWrapper.publishMsg(forwardMessage);
    }

}
