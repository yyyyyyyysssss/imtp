package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.Packet;
import org.imtp.common.packet.TextMessage;
import org.imtp.server.utils.CacheUtil;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
public class SendMessageHandler extends SimpleChannelInboundHandler<Packet> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        switch (packet.getHeader().getCmd()){
            case TEXT_MSG_REQ :
                TextMessage textMessage = (TextMessage) packet;
                System.out.println(textMessage.getMessage());
                channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED));

                Channel channel = CacheUtil.getChannel(textMessage.getReceiver());
                if(channel != null){
                    channel.writeAndFlush(textMessage);
                }

                break;
            case TEXT_MSG_RES:
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
