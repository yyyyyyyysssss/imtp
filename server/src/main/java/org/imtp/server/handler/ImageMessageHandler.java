package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.ImageMessage;
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
public class ImageMessageHandler extends AbstractHandler<ImageMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ImageMessage imageMessage) {
        //响应已送达报文
        channelHandlerContext.channel().writeAndFlush(new MessageStateResponse(MessageState.DELIVERED,imageMessage));
        //转发
        List<Long> offlineReceivers = new ArrayList<>();
        List<Long> receivers = getReceivers(imageMessage);
        for(Long receiver : receivers){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiver.toString());
            if(channel != null && channel.isActive()){
                if(!receiver.equals(imageMessage.getSender())){
                    channel.writeAndFlush(imageMessage);
                }
            }else {
                offlineReceivers.add(receiver);
            }
        }
        //落库
        channelHandlerContext.channel().eventLoop().execute(() -> {
            Message message = new Message(imageMessage);
            message.setContent(imageMessage.getPath());
            chatService.saveMessage(message);
            if (!offlineReceivers.isEmpty()){
                List<OfflineMessage> offlineMessages = new ArrayList<>();
                for (Long receiver : offlineReceivers){
                    OfflineMessage offlineMessage = new OfflineMessage(message.getId(),receiver);
                    offlineMessages.add(offlineMessage);
                }
                chatService.saveOfflineMessage(offlineMessages);
            }

        });

    }
}
