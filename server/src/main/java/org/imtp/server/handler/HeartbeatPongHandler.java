package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.HeartbeatPongMessage;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/12 17:01
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartbeatPongHandler extends AbstractHandler<HeartbeatPongMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatPongMessage heartbeatPongMessage) {
        log.debug("pong channelId: {}",ctx.channel().id().asLongText());
    }
}
