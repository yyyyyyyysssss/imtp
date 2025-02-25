package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.AbstractSignalingMessage;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/25 17:05
 */
public abstract class AbstractSignalingHandler<T extends AbstractSignalingMessage> extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AbstractSignalingMessage msg) {
        forwardMessage(channelHandlerContext,msg);
    }
}
