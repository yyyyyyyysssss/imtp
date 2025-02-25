package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.response.Result;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.mq.ForwardMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            MessageDTO messageDTO = new MessageDTO(msg);
            messageDTO.setContent(msg.getMessage());
            Result<Long> result = webApi.message(messageDTO);
            if (result.isSucceed()){
                log.debug("save message succeed : {}",result.getMessage());
            }else {
                log.warn("save message error : {}",result.getMessage());
            }
        });
    }
}
