package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/21 12:58
 */
public class CommandHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(packet instanceof CommandPacket commandPacket){
            Header header = commandPacket.getHeader();
            Command cmd = header.getCmd();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            switch (cmd) {
                case LOGIN_REQ:
                    packet = new LoginRequest(byteBuf, header);
                    channelHandlerContext.pipeline().addLast(new LoginHandler()).fireChannelRead(packet);
                    break;
                case TEXT_MSG_REQ:
                    packet = new TextMessage(byteBuf, header);
                    channelHandlerContext.pipeline().addLast(new TextMessageHandler()).fireChannelRead(packet);
                    break;
                case TEXT_MSG_RES:
                    packet = new DefaultMessageResponse(byteBuf, header);
                    break;
            }
        }else {
            channelHandlerContext.fireChannelRead(packet);
        }

    }

}
