package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;

import java.net.SocketException;

/**
 * @Description 用于命令分发到具体处理器处理
 * @Author ys
 * @Date 2024/4/21 12:58
 */
@Slf4j
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
                case PRIVATE_MSG:
                    packet = new PrivateChatMessage(byteBuf, header);
                    channelHandlerContext.pipeline().addLast(new PrivateChatMessageHandler()).fireChannelRead(packet);
                    break;
                case GROUP_CHAT_MSG:
                    packet = new GroupChatMessage(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(new GroupChatMessageHandler()).fireChannelRead(packet);
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的操作");
            }
        }else {
            channelHandlerContext.fireChannelRead(packet);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException){

        }else {

        }
    }
}
