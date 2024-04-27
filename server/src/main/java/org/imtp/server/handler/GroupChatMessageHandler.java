package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.DefaultMessageResponse;
import org.imtp.common.packet.GroupChatMessage;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.service.ChatService;
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
public class GroupChatMessageHandler extends SimpleChannelInboundHandler<GroupChatMessage> {

    @Resource
    private ChatService chatService;

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
        System.out.println(groupChatMessage.getMessage());
        channelHandlerContext.channel().writeAndFlush(new DefaultMessageResponse(MessageState.DELIVERED,groupChatMessage.getHeader()));
        Long receiver = groupChatMessage.getReceiver();
        List<Long> longs = testGroupChatMap.get(receiver);
        for(Long l : longs){
            Channel channel = ChannelContextHolder.createChannelContext().getChannel(l.toString());
            if(channel != null && channel.isActive()){
                if(!l.equals(groupChatMessage.getSender())){
                    channel.writeAndFlush(groupChatMessage);
                }
            }else {
                //记录消息，等待用户上线后推送

            }
        }
    }
}
