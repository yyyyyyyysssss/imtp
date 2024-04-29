package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.Command;
import org.imtp.common.packet.*;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Component;

import java.net.SocketException;

/**
 * @Description 用于命令分发到具体处理器处理
 * @Author ys
 * @Date 2024/4/21 12:58
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class CommandHandler extends SimpleChannelInboundHandler<Packet> {

    @Resource
    private PrivateChatMessageHandler privateChatMessageHandler;

    @Resource
    private GroupChatMessageHandler groupChatMessageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(packet instanceof CommandPacket commandPacket){
            Header header = commandPacket.getHeader();
            Command cmd = header.getCmd();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            switch (cmd) {
                case PULL_FRIEND_REQ:
                    packet = new PullFriendRequest(byteBuf,header);
                    break;
                case PRIVATE_CHAT_MSG:
                    packet = new PrivateChatMessage(byteBuf, header);
                    channelHandlerContext.pipeline().addLast(privateChatMessageHandler).fireChannelRead(packet);
                    break;
                case GROUP_CHAT_MSG:
                    packet = new GroupChatMessage(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(groupChatMessageHandler).fireChannelRead(packet);
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
        AttributeKey<Long> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        Long loginUser = ctx.channel().attr(attributeKey).get();
        if (cause instanceof SocketException){
            log.warn("用户[{}]已断开连接",loginUser);
        }else {
            log.error("exception message",cause);
        }
        //移除
        ChannelContextHolder.createChannelContext().removeChannel(loginUser.toString());
    }
}
