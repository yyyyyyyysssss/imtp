package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.server.utils.CacheUtil;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
public class TextMessageHandler extends SimpleChannelInboundHandler<TextMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextMessage textMessage) {
        System.out.println(textMessage.getMessage());
        channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED));
        Channel channel = CacheUtil.getChannel(textMessage.getReceiver());
        if(channel != null){
            channel.writeAndFlush(textMessage);
        }else {
            //记录消息，等待用户上线后推送
        }
    }
}
