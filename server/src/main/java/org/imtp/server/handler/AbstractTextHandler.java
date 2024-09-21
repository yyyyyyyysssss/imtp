package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.MessageState;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.common.response.Result;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.context.ChannelSession;
import org.imtp.server.mq.ForwardMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 15:07
 */
@Slf4j
public abstract class AbstractTextHandler<T extends AbstractTextMessage>  extends AbstractHandler<T>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        //响应已送达报文
        ChannelSession senderChannelSession = ChannelContextHolder.channelContext().getChannel(ctx.channel().id().asLongText());
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
            MessageDTO messageDTO = new MessageDTO(msg);
            messageDTO.setContent(msg.getMessage());
            Result<Long> result = webApi.message(messageDTO);
            if (result.isSucceed()){
                Long messageId = result.getData();
                if (!offlineReceivers.isEmpty()){
                    List<OfflineMessageDTO> offlineMessages = new ArrayList<>();
                    for (String receiver : offlineReceivers){
                        OfflineMessageDTO offlineMessage = new OfflineMessageDTO(messageId,Long.parseLong(receiver));
                        offlineMessages.add(offlineMessage);
                    }
                    webApi.offlineMessage(offlineMessages);
                }
            }else {
                log.warn("save message error : {}",result.getMessage());
            }
        });
    }
}
