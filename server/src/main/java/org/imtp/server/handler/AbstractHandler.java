package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.HeartbeatPingMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.response.Result;
import org.imtp.server.config.RedisWrapper;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.restclient.ChatApi;
import org.imtp.server.mq.ForwardMessage;
import org.imtp.server.service.UserStatusService;

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
    protected UserStatusService userStatusService;

    @Resource
    protected ChatApi chatApi;

    @Resource
    protected RedisWrapper redisWrapper;

    protected void forwardMessage(ChannelHandlerContext ctx, Packet msg){
        log.debug("forward message from:[{}] to:[{}] message type:[{}]",msg.getSender(),msg.getReceiver(),msg.messageType());
        //转发
        List<String> forwardChannelIds = new ArrayList<>();
        List<String> receiverUserIds = fetchReceiverUserIdByPacket(msg);
        //根据用户id获取关联的channel
        Map<String, Set<String>> map = userStatusService.fetchActiveChannelIdByUserIds(receiverUserIds);
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

    //消息落库
    protected Long saveMessage(Packet packet,String content){
        MessageDTO messageDTO = new MessageDTO(packet);
        messageDTO.setContent(content);
        Result<Long> result = chatApi.message(messageDTO);
        if (result.isSucceed()){
            log.debug("save message succeed : {}",result.getMessage());
            return result.getData();
        }else {
            log.error("save message error : {}",result.getMessage());
            return null;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent event){
            ChannelSession channelSession = ChannelContextHolder.channelContext().getChannel(ctx.channel());
            switch (event.state()){
                //读空闲 长时间没有收到客户端数据
                case READER_IDLE:
                    ctx.channel().close();
                    break;
                //写空闲 长时间没有向客户端发送数据 发送PING消息
                case WRITER_IDLE:
                    channelSession.sendMessage(new HeartbeatPingMessage());
                    break;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("channelInactive:{}",ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        unbind(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exception message",cause);
        Channel channel = ctx.channel();
        unbind(channel);
        ctx.close();
    }

    //channel与用户进行绑定
    protected void bind(ChannelSession channelSession){
        Channel channel = channelSession.channel();
        String userId = channelSession.userId();
        //保存channel
        ChannelContextHolder.channelContext().addChannel(channelSession.id(),channelSession);
        //建立channel与用户之间的关系
        channel.attr(AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER)).set(userId);
        log.info("用户:{} 已上线",userId);
        userStatusService.userOnline(userId, channelSession.id());
    }

    //channel与用户解绑
    protected void unbind(Channel channel){
        AttributeKey<String> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        String userId = channel.attr(attributeKey).get();
        //移除channel
        ChannelContextHolder.channelContext().removeChannel(channel.id().asLongText());
        log.warn("用户[{}]已断开连接",userId);
        userStatusService.userOffline(userId, channel.id().asLongText());
    }

    protected List<String> fetchReceiverUserIdByPacket(Packet packet){
        List<String> receivers;
        if(packet.isGroup()){
            Result<List<String>> result = chatApi.userIds(packet.getReceiver().toString());
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
