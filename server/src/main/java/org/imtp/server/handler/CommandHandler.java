package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Component;

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
    private VideoMessageHandler videoMessageHandler;

    @Resource
    private FileMessageHandler fileMessageHandler;

    @Resource
    private UserFriendshipHandler userFriendshipHandler;

    @Resource
    private UserGroupRelationshipHandler userGroupRelationshipHandler;

    @Resource
    private OfflineMessageHandler offlineMessageHandler;

    @Resource
    private UserSessionHandler userSessionHandler;

    @Resource
    private ChatService chatService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if(packet instanceof CommandPacket commandPacket){
            Header header = commandPacket.getHeader();
            Command cmd = header.getCmd();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(commandPacket.getBytes());
            try {
                switch (cmd) {
                    case TEXT_MESSAGE:
                        packet = new TextMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(TextMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(textMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case IMAGE_MESSAGE:
                        packet = new ImageMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(ImageMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(imageMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case VIDEO_MESSAGE:
                        packet = new VideoMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(VideoMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(videoMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case FILE_MESSAGE:
                        packet = new FileMessage(byteBuf,header).additionTimestamp();
                        if (channelHandlerContext.pipeline().get(FileMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(fileMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case FRIENDSHIP_REQ:
                        packet = new FriendshipRequest(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(UserFriendshipHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(userFriendshipHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case GROUP_RELATIONSHIP_REQ:
                        packet = new GroupRelationshipRequest(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(UserGroupRelationshipHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(userGroupRelationshipHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case OFFLINE_MSG_REQ:
                        packet = new OfflineMessageRequest(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(OfflineMessageHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(offlineMessageHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    case USER_SESSION_REQ:
                        packet = new UserSessionRequest(byteBuf,header);
                        if (channelHandlerContext.pipeline().get(UserSessionHandler.class) == null){
                            channelHandlerContext.pipeline().addLast(userSessionHandler).fireChannelRead(packet);
                        }else {
                            channelHandlerContext.fireChannelRead(packet);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("不支持的操作");
                }
            }finally {
                byteBuf.release();
            }

        }else {
            channelHandlerContext.fireChannelRead(packet);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("im server channelInactive:{}",ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
        log.error("exception message",cause);
        ctx.close();
    }

    private void channelInactiveHandle(Channel channel){
        AttributeKey<String> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        String userId = channel.attr(attributeKey).get();
        log.warn("用户[{}]已断开连接",userId);
        //移除用户在线状态
        chatService.userOffline(userId, channel.id().asLongText());
        //移除
        ChannelContextHolder.channelContext().removeChannel(channel.id().asLongText());
    }

}
