package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.controller.ConsoleController;
import org.imtp.client.enums.ClientType;
import org.imtp.client.model.MessageModel;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.utils.JsonUtil;

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
            case FRIENDSHIP_RES:
                publishMessage(new FriendshipResponse(byteBuf,header));
                break;
            case GROUP_RELATIONSHIP_RES:
                publishMessage(new GroupRelationshipResponse(byteBuf,header));
                break;
            case OFFLINE_MSG_RES:
                publishMessage(new OfflineMessageResponse(byteBuf,header));
                break;
            case USER_SESSION_RES:
                publishMessage(new UserSessionResponse(byteBuf,header));
                break;
            case TEXT_MESSAGE:
                publishMessage(new TextMessage(byteBuf,header));
                break;
            case MSG_RES:
                publishMessage(new MessageStateResponse(byteBuf,header));
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }

    @Override
    public MessageModel getNextModel() {
        return null;
    }
}
