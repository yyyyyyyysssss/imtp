package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.PrivateChatMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.idwork.IdGen;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
@Component
@ChannelHandler.Sharable
public class PrivateChatMessageHandler extends AbstractHandler<PrivateChatMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PrivateChatMessage privateChatMessage) {
        boolean tempOfflineFlag = false;
        //响应已送达报文
        channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED, privateChatMessage.getHeader()));
        //转发消息到目标用户
        Channel channel = ChannelContextHolder.createChannelContext().getChannel(privateChatMessage.getReceiver().toString());
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(privateChatMessage);
        } else {
            tempOfflineFlag  = true;
        }
        final boolean offlineFlag = tempOfflineFlag;
        //记录消息
        channelHandlerContext.channel().eventLoop().execute(() -> {
            Message message = new Message(privateChatMessage);
            message.setContent(privateChatMessage.getMessage());
            chatService.saveMessage(message);
            //记录离线消息
            if(offlineFlag){
                OfflineMessage offlineMessage = new OfflineMessage(message.getId(),message.getReceiverUserId());
                chatService.saveOfflineMessage(List.of(offlineMessage));
            }
        });
    }
}
