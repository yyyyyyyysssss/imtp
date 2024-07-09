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
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
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
    private TextMessageHandler textMessageHandler;

    @Resource
    private ImageMessageHandler imageMessageHandler;

    @Resource
    private UserFriendshipHandler userFriendshipHandler;

    @Resource
    private UserGroupRelationshipHandler userGroupRelationshipHandler;

    @Resource
    private OfflineMessageHandler offlineMessageHandler;

    @Resource
    private UserSessionHandler userSessionHandler;

    @Resource
    private RedisWrapper redisWrapper;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(packet instanceof CommandPacket commandPacket){
            Header header = commandPacket.getHeader();
            Command cmd = header.getCmd();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            switch (cmd) {
                case FRIENDSHIP_REQ:
                    packet = new FriendshipRequest(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(userFriendshipHandler).fireChannelRead(packet);
                    break;
                case GROUP_RELATIONSHIP_REQ:
                    packet = new GroupRelationshipRequest(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(userGroupRelationshipHandler).fireChannelRead(packet);
                    break;
                case OFFLINE_MSG_REQ:
                    packet = new OfflineMessageRequest(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(offlineMessageHandler).fireChannelRead(packet);
                    break;
                case USER_SESSION_REQ:
                    packet = new UserSessionRequest(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(userSessionHandler).fireChannelRead(packet);
                    break;
                case TEXT_MESSAGE:
                    packet = new TextMessage(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(textMessageHandler).fireChannelRead(packet);
                    break;
                case IMAGE_MESSAGE:
                    packet = new ImageMessage(byteBuf,header);
                    channelHandlerContext.pipeline().addLast(imageMessageHandler).fireChannelRead(packet);
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
        //移除用户在线状态
        redisWrapper.userOffline(loginUser.toString());
    }
}
