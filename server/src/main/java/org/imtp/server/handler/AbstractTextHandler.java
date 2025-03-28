package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 15:07
 */
@Slf4j
public abstract class AbstractTextHandler<T extends AbstractTextMessage>  extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) {
        //响应已送达报文
        ChannelSession senderChannelSession = ChannelContextHolder.channelContext().getChannel(ctx.channel().id().asLongText());
        senderChannelSession.sendMessage(new MessageStateResponse(MessageState.DELIVERED,msg));
        //转发
        forwardMessage(ctx,msg);
        //消息落库
        ctx.channel().eventLoop().execute(() -> {
            saveMessage(msg,msg.getMessage());
        });
    }
}
