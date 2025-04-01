package org.imtp.server.service;

import io.netty.channel.Channel;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.service.impl.UserStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/1 11:10
 */
@ExtendWith(MockitoExtension.class)
public class UserStatusServiceTest {

    @Mock
    private RedisWrapper redisWrapper;

    @InjectMocks
    private UserStatusService userStatusService = new UserStatusServiceImpl();

    @BeforeEach
    void setup(){

    }

    @Test
    public void testUserOnline() {
        doNothing()
                .when(redisWrapper)
                .addSet(anyString(), anyString());
        userStatusService.userOnline(anyString(), anyString());
    }

    @Test
    public void testUserOffline() {
        doNothing()
                .when(redisWrapper)
                .removeSet(anyString(), anyString());
        userStatusService.userOffline(anyString(), anyString());
    }

}
