package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.controller.MessageController;
import org.imtp.common.enums.Command;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.*;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
@Slf4j
public class ClientCmdHandlerHandler extends AbstractMessageModelHandler<Packet> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        new MessageController(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        CommandPacket commandPacket = (CommandPacket)packet;
        Header header = commandPacket.getHeader();
        Command cmd = header.getCmd();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
        switch (cmd){
            case PRIVATE_CHAT_MSG :
                setMessage(new PrivateChatMessage(byteBuf,header));
                break;
            case GROUP_CHAT_MSG:
                setMessage(new GroupChatMessage(byteBuf,header));
                break;
            case MSG_RES:
                MessageStateResponse response = new MessageStateResponse(byteBuf,header);
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
