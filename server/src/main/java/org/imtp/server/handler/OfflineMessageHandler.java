package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.imtp.common.packet.OfflineMessageRequest;
import org.imtp.common.packet.OfflineMessageResponse;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.server.entity.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/30 13:57
 */
@Component
@ChannelHandler.Sharable
public class OfflineMessageHandler extends AbstractHandler<OfflineMessageRequest>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, OfflineMessageRequest offlineMessageRequest) throws Exception {
        List<Message> messages = chatService.findOfflineMessageByUserId(offlineMessageRequest.getSender());
        if(messages.isEmpty()){
            channelHandlerContext.channel().writeAndFlush(new OfflineMessageResponse(offlineMessageRequest.getSender()));
            return;
        }
        List<OfflineMessageInfo> offlineMessageInfos = new ArrayList<>();
        for (Message message : messages){
            OfflineMessageInfo offlineMessageInfo = OfflineMessageInfo
                    .builder()
                    .id(message.getId())
                    .sender(message.getSenderUserId())
                    .receiver(message.getReceiverUserId())
                    .type(message.getType())
                    .content(message.getContent())
                    .sendTime(message.getSendTime().getTime())
                    .build();
            offlineMessageInfos.add(offlineMessageInfo);
        }
        channelHandlerContext.channel().writeAndFlush(new OfflineMessageResponse(offlineMessageRequest.getSender(),offlineMessageInfos));
    }


}
