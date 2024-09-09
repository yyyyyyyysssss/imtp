package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.entity.Message;
import org.imtp.server.entity.OfflineMessage;
import org.imtp.server.mq.ForwardMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 15:07
 */
public abstract class AbstractTextHandler<T extends AbstractTextMessage>  extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        //响应已送达报文
        ChannelSession senderChannelSession = ChannelContextHolder.getChannelContext().getChannel(ctx.channel().id().asLongText());
        senderChannelSession.sendMessage(new MessageStateResponse(MessageState.DELIVERED,msg));
        //转发
        List<String> forwardChannelIds = new ArrayList<>();
        List<String> offlineReceivers = new ArrayList<>();
        List<String> receiverUserIds = fetchReceiverUserIdByPacket(msg);
        //根据用户id获取关联的channel
        Map<String,Set<String>> map = chatService.fetchActiveChannelIdByUserIds(receiverUserIds);
        for (String userId : map.keySet()){
            Set<String> channelIds = map.get(userId);
            //null表示用户未上线
            if(channelIds == null){
                offlineReceivers.add(userId);
                continue;
            }
            for(String channelId : channelIds){
                ChannelSession channel = ChannelContextHolder.getChannelContext().getChannel(channelId);
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
            ByteBuf byteBuf = Unpooled.buffer();
            try {
                msg.encodeAsByteBuf(byteBuf);
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                ForwardMessage forwardMessage = new ForwardMessage(forwardChannelIds, msg);
                redisWrapper.publishMsg(forwardMessage);
            }finally {
                byteBuf.release();
            }
        }

        //离线用户消息落库
        ctx.channel().eventLoop().execute(() -> {
            Message message = new Message(msg);
            message.setContent(msg.getMessage());
            chatService.saveMessage(message);
            if (!offlineReceivers.isEmpty()){
                List<OfflineMessage> offlineMessages = new ArrayList<>();
                for (String receiver : offlineReceivers){
                    OfflineMessage offlineMessage = new OfflineMessage(message.getId(),Long.parseLong(receiver));
                    offlineMessages.add(offlineMessage);
                }
                chatService.saveOfflineMessage(offlineMessages);
            }

        });
    }
}
