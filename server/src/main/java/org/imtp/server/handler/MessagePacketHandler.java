package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.imtp.common.enums.MessageTypeV2;
import org.imtp.common.packet.base.MessagePacket;
import org.imtp.common.packet.base.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:43
 */
@Component
@ChannelHandler.Sharable
public class MessagePacketHandler extends AbstractHandler<MessagePacket> {

    @Resource
    private Map<String, ForwardMessageHandler<? extends Packet>> handlerMap;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacket messagePacket) {
        MessageTypeV2 messageType = messagePacket.getMessageType();
        ForwardMessageHandler<? extends Packet> forwardMessageHandler = handlerMap.get(messageType.name().toLowerCase());
        if (ctx.pipeline().get(forwardMessageHandler.getClass()) == null){
            ctx.pipeline().addLast(forwardMessageHandler).fireChannelRead(messagePacket);
        }else {
            ctx.fireChannelRead(messagePacket);
        }
        // 响应已送达
        acknowledgment(ctx,messagePacket.getReceiver(),messagePacket.getAckId());
    }
}
