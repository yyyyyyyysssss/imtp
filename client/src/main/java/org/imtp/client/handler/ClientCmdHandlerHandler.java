package org.imtp.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.controller.MessageController;
import org.imtp.client.model.Model;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/8 14:53
 */
@Slf4j
public class ClientCmdHandlerHandler extends AbstractMessageModelHandler<Packet> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //发送拉取好友关系请求
        ctx.channel().writeAndFlush(new FriendshipRequest(ClientContextHolder.clientContext().id()));
        //拉取群组信息
        ctx.channel().writeAndFlush(new GroupRelationshipRequest(ClientContextHolder.clientContext().id()));
        //离线消息拉取
        ctx.channel().writeAndFlush(new OfflineMessageRequest(ClientContextHolder.clientContext().id()));
        new MessageController(this);
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
            case TEXT_MESSAGE:
                publishMessage(new TextMessage(byteBuf,header));
                break;
            case MSG_RES:
                MessageStateResponse response = new MessageStateResponse(byteBuf,header);
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作");
        }
    }
}
