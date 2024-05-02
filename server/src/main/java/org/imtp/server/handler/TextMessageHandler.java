package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component
@ChannelHandler.Sharable
public class TextMessageHandler extends AbstractHandler<TextMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextMessage textMessage) {
        Message message = new Message(textMessage);
        message.setContent(textMessage.getMessage());
        //响应已送达报文
        channelHandlerContext.channel().writeAndFlush(new MessageStateResponse(MessageState.DELIVERED,textMessage.getHeader()));

        final List<OfflineMessage> offlineMessages = new ArrayList<>();
        if(textMessage.isGroup()){
            List<Long> receiverUserIds = chatService.findUserIdByGroupId(textMessage.getReceiver());
            for(Long receiverUserId : receiverUserIds){
                Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiverUserId.toString());
                if(channel != null && channel.isActive()){
                    if(!receiverUserId.equals(textMessage.getSender())){
                        channel.writeAndFlush(textMessage);
                    }
                }else {
                    //记录消息，等待用户上线后推送
                    OfflineMessage offlineMessage = new OfflineMessage(message.getId(),receiverUserId);
                    offlineMessages.add(offlineMessage);
                }
            }
        }else {
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(textMessage.getReceiver().toString());
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(textMessage);
            }else {
                OfflineMessage offlineMessage = new OfflineMessage(message.getId(),message.getReceiverUserId());
                offlineMessages.add(offlineMessage);
            }
        }
        //查询群组关联的用户并推送
        channelHandlerContext.channel().eventLoop().execute(() -> {
            chatService.saveMessage(message);
            if(!offlineMessages.isEmpty()){
                chatService.saveOfflineMessage(offlineMessages);
            }
        });

    }
}
