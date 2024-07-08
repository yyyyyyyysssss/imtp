package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 15:07
 */
public abstract class AbstractTextHandler<T extends AbstractTextMessage>  extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        //响应已送达报文
        ctx.channel().writeAndFlush(new MessageStateResponse(MessageState.DELIVERED,msg));
        //转发
        List<Long> offlineReceivers = new ArrayList<>();
        List<Long> receivers = getReceivers(msg);
        for(Long receiver : receivers){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiver.toString());
            if(channel != null && channel.isActive()){
                if(!receiver.equals(msg.getSender())){
                    channel.writeAndFlush(msg);
                }
            }else {
                offlineReceivers.add(receiver);
            }
        }
        //落库
        ctx.channel().eventLoop().execute(() -> {
            Message message = new Message(msg);
            message.setContent(msg.getMessage());
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
