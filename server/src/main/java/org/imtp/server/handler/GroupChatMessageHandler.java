package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.GroupChatMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component
@ChannelHandler.Sharable
public class GroupChatMessageHandler extends AbstractHandler<GroupChatMessage> {

    //用于测试群组聊天
    private static final Map<Long, List<Long>> testGroupChatMap = new HashMap<>();
    static {
        List<Long> list = new ArrayList<>();
        list.add(147L);
        list.add(258L);
        list.add(369L);
        testGroupChatMap.put(9527L,list);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupChatMessage groupChatMessage) {
        Message message = new Message(groupChatMessage);
        message.setContent(groupChatMessage.getMessage());
        //响应已送达报文
        channelHandlerContext.channel().writeAndFlush(new MessageStateResponse(MessageState.DELIVERED,groupChatMessage.getHeader()));
        //查询群组关联的用户并推送
        final List<OfflineMessage> offlineMessages = new ArrayList<>();
        List<Long> receiverUserIds = chatService.findUserIdByGroupId(groupChatMessage.getReceiver());
        for(Long receiverUserId : receiverUserIds){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(receiverUserId.toString());
            if(channel != null && channel.isActive()){
                if(!receiverUserId.equals(groupChatMessage.getSender())){
                    channel.writeAndFlush(groupChatMessage);
                }
            }else {
                //记录消息，等待用户上线后推送
                OfflineMessage offlineMessage = new OfflineMessage(message.getId(),receiverUserId);
                offlineMessages.add(offlineMessage);
            }
        }
        channelHandlerContext.channel().eventLoop().execute(() -> {
            chatService.saveMessage(message);
            if(!offlineMessages.isEmpty()){
                chatService.saveOfflineMessage(offlineMessages);
            }
        });

    }
}
