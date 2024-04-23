package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.PrivateChatMessage;
import org.imtp.server.context.ChannelContextHolder;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
public class PrivateChatMessageHandler extends SimpleChannelInboundHandler<PrivateChatMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PrivateChatMessage privateChatMessage) {
        System.out.println(privateChatMessage.getMessage());
        channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED,privateChatMessage.getHeader()));
        Channel channel = ChannelContextHolder.createChannelContext().getChannel(privateChatMessage.getReceiver().toString());
        if(channel != null && channel.isActive()){
            channel.writeAndFlush(privateChatMessage);
        }else {
            //记录消息，等待用户上线后推送
        }
    }
}
