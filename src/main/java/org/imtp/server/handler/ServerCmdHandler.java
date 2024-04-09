package org.imtp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.enums.MessageState;
import org.imtp.packet.DefaultMessageResponse;
import org.imtp.packet.Packet;
import org.imtp.packet.TextMessage;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
public class ServerCmdHandler extends SimpleChannelInboundHandler<Packet> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        switch (packet.getHeader().getCmd()){
            case TEXT_MSG_REQ :
                TextMessage textMessage = (TextMessage) packet;
                System.out.println(textMessage.getMessage());

                channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED));
                break;
            case TEXT_MSG_RES:
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
