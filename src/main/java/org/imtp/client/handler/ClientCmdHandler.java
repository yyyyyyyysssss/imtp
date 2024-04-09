package org.imtp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.enums.Command;
import org.imtp.packet.DefaultMessageResponse;
import org.imtp.packet.Packet;
import org.imtp.packet.TextMessage;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
public class ClientCmdHandler extends SimpleChannelInboundHandler<Packet> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(new TextMessage("hello server",123456789,987654321, Command.TEXT_MSG_REQ));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        switch (packet.getHeader().getCmd()){
            case TEXT_MSG_REQ :
                TextMessage textMessage = (TextMessage) packet;
                System.out.println(textMessage.getMessage());
                break;
            case TEXT_MSG_RES:
                DefaultMessageResponse response = (DefaultMessageResponse)packet;
                System.out.println(response.getState().name());
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
