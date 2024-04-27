package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.PrivateChatMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.enums.OfflineMsgEnum;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Component;


/**
 * @Description
 * @Author ys
 * @Date 2024/4/7 14:53
 */
@Component
@ChannelHandler.Sharable
public class PrivateChatMessageHandler extends SimpleChannelInboundHandler<PrivateChatMessage> {

    @Resource
    private ChatService chatService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PrivateChatMessage privateChatMessage) {
        System.out.println(privateChatMessage.getMessage());
        channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED,privateChatMessage.getHeader()));
        Channel channel = ChannelContextHolder.createChannelContext().getChannel(privateChatMessage.getReceiver().toString());
        if(channel != null && channel.isActive()){
            channel.writeAndFlush(privateChatMessage);
        }else {
            channelHandlerContext.channel().eventLoop().execute(() -> {
                //记录消息，等待用户上线后推送
                OfflineMessage offlineMessage = new OfflineMessage(privateChatMessage,OfflineMsgEnum.PRIVATE_CHAT_TYPE,privateChatMessage.getMessage());
                chatService.saveOfflineMessage(offlineMessage);
            });
        }
    }
}
