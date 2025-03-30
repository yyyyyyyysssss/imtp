package org.imtp.desktop.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.desktop.Client;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.controller.ConsoleController;
import org.imtp.desktop.enums.ClientType;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientCmdHandlerHandler extends AbstractMessageModelHandler<Packet> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Client client = ClientContextHolder.clientContext().client();
        if (client.getClientType().equals(ClientType.CONSOLE)){
            ConsoleController.getInstance(this);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        Command cmd = header.getCmd();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (cmd){
            case TEXT_MESSAGE:
                publishMessage(new TextMessage(byteBuf,header));
                break;
            case IMAGE_MESSAGE:
                publishMessage(new ImageMessage(byteBuf,header));
                break;
            case VIDEO_MESSAGE:
                publishMessage(new VideoMessage(byteBuf,header));
                break;
            case FILE_MESSAGE:
                publishMessage(new FileMessage(byteBuf,header));
                break;
            case HEARTBEAT_PING:
                channelHandlerContext.channel().writeAndFlush(new HeartbeatPongMessage());
                break;
            case MSG_RES:
                publishMessage(new MessageStateResponse(byteBuf,header));
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
