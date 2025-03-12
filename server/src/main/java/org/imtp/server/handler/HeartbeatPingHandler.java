package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.HeartbeatPingMessage;
import org.imtp.common.packet.HeartbeatPongMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/12 17:01
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartbeatPingHandler extends AbstractHandler<HeartbeatPingMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartbeatPingMessage heartbeatPingMessage) {
        ChannelSession channelSession = ChannelContextHolder.channelContext().getChannel(ctx.channel());
        channelSession.sendMessage(new HeartbeatPongMessage());
        log.debug("channelId:{} ping",channelSession.id());
    }
}
