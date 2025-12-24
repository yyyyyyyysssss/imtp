package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 15:07
 */
@Slf4j
public abstract class ForwardMessageHandler<T extends Packet>  extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) {
        // 转发前
        preForward(ctx, msg);
        //转发
        forwardMessage(ctx,msg);
        // 转发后
        postForward(ctx, msg);
    }

    // 钩子方法，允许子类扩展转发前的逻辑
    protected void preForward(ChannelHandlerContext ctx, T msg) {

    }

    // 钩子方法，允许子类扩展转发后的逻辑
    protected void postForward(ChannelHandlerContext ctx, T msg) {

    }


}
