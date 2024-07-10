package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.mq.ForwardMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> offlineReceivers = new ArrayList<>();
        List<Long> receivers = getReceivers(msg);
        for(Long receiver : receivers){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiver.toString());
            if(channel != null && channel.isActive()){
                if(!receiver.equals(msg.getSender())){
                    channel.writeAndFlush(msg);
                }
            }else {
                offlineReceivers.add(receiver.toString());
            }
        }

        //获取在线且不在当前服务器的用户并发布
        List<String> userOnline = chatService.batchGetUserOnline(offlineReceivers);
        ByteBuf byteBuf = Unpooled.buffer();
        try {
            msg.encodeAsByteBuf(byteBuf);
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            ForwardMessage forwardMessage = new ForwardMessage(userOnline, bytes);
            redisWrapper.publishMsg(forwardMessage);
        }finally {
            byteBuf.release();
        }

        //离线用户消息落库
        ctx.channel().eventLoop().execute(() -> {
            Message message = new Message(msg);
            message.setContent(msg.getMessage());
            chatService.saveMessage(message);
            //将在线的用户移除
            offlineReceivers.removeAll(userOnline);
            if (!offlineReceivers.isEmpty()){
                List<OfflineMessage> offlineMessages = new ArrayList<>();
                for (String receiver : offlineReceivers){
                    OfflineMessage offlineMessage = new OfflineMessage(message.getId(),Long.parseLong(receiver));
                    offlineMessages.add(offlineMessage);
                }
                chatService.saveOfflineMessage(offlineMessages);
            }

        });
    }
}
