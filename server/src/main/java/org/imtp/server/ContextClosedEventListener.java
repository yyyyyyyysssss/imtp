package org.imtp.server;

import jakarta.annotation.Resource;
import org.imtp.server.service.UserStatusService;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Description 服务停止时事件
 * @Author ys
 * @Date 2024/9/9 13:39
 */
@Component
public class ContextClosedEventListener {

    @Resource
    private UserStatusService userStatusService;

    @EventListener(ContextClosedEvent.class)
    public void onContextClosedEvent(ContextClosedEvent contextClosedEvent) {
        userStatusService.allUserOffline();
    }

}
