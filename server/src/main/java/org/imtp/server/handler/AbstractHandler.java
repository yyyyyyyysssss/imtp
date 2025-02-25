package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.response.Result;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.feign.WebApi;
import org.imtp.server.mq.ForwardMessage;
import org.imtp.server.service.ChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 16:48
 */
@Slf4j
public abstract class AbstractHandler<I> extends SimpleChannelInboundHandler<I> {

    @Resource
    protected ChatService chatService;

    @Resource
    protected WebApi webApi;

    @Resource
    protected RedisWrapper redisWrapper;

    protected void forwardMessage(ChannelHandlerContext ctx, Packet msg){
        //转发
        List<String> forwardChannelIds = new ArrayList<>();
        List<String> receiverUserIds = fetchReceiverUserIdByPacket(msg);
        //根据用户id获取关联的channel
        Map<String, Set<String>> map = chatService.fetchActiveChannelIdByUserIds(receiverUserIds);
        for (String userId : map.keySet()){
            Set<String> channelIds = map.get(userId);
            //null表示用户未上线
            if(channelIds == null){
                continue;
            }
            for(String channelId : channelIds){
                ChannelSession channel = ChannelContextHolder.channelContext().getChannel(channelId);
                if(channel != null){
                    if(!channel.id().equals(ctx.channel().id().asLongText())){
                        channel.sendMessage(msg);
                    }
                }else {
                    forwardChannelIds.add(channelId);
                }
            }
        }
        //发布
        if(!forwardChannelIds.isEmpty()){
            ForwardMessage forwardMessage = new ForwardMessage(forwardChannelIds, msg);
            redisWrapper.publishMsg(forwardMessage);
        }
    }

    protected void channelInactiveHandle(Channel channel){
        AttributeKey<String> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        String userId = channel.attr(attributeKey).get();
        log.warn("用户[{}]已断开连接",userId);
        //移除用户在线状态
        chatService.userOffline(userId, channel.id().asLongText());
        //移除
        ChannelContextHolder.channelContext().removeChannel(channel.id().asLongText());
    }

    protected List<String> fetchReceiverUserIdByPacket(Packet packet){
        List<String> receivers;
        if(packet.isGroup()){
            Result<List<String>> result = webApi.userIds(packet.getReceiver().toString());
            if (result.isSucceed()){
                receivers = result.getData();
            }else {
                receivers = List.of();
            }
        }else {
            receivers = List.of(packet.getReceiver().toString());
        }
        return receivers;
    }

}
