package org.imtp.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.CommandPacket;
import org.imtp.common.packet.WebSocketAdapterMessage;
import org.imtp.common.packet.WebSocketMessage;
import org.imtp.common.packet.WebSocketSignalingAdapterMessage;
import org.imtp.common.packet.base.Header;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.utils.CRC16Util;
import org.imtp.common.utils.JsonUtil;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/7 20:14
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketAdapterHandler extends AbstractHandler<WebSocketFrame> {

    @Resource
    private CommandHandler commandHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof TextWebSocketFrame textWebSocketFrame) {
            String text = textWebSocketFrame.text();
            WebSocketMessage webSocketMessage = JsonUtil.parseObject(text, WebSocketMessage.class);
            Packet webSocketAdapterMessage = createWebSocketAdapterMessage(webSocketMessage);
            Header header = webSocketAdapterMessage.getHeader();
            header.setLength(webSocketAdapterMessage.getBodyLength());
            ByteBuf byteBuf = Unpooled.buffer();
            byte[] data;
            short receiveVerify;
            try {
                webSocketAdapterMessage.encodeBodyAsByteBuf(byteBuf);
                data = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(data);
                receiveVerify = CRC16Util.calculateCRC(data);
            }finally {
                byteBuf.release();
            }
            Packet packet = new CommandPacket(header, data, receiveVerify);
            if (ctx.pipeline().get(CommandHandler.class) == null) {
                ctx.pipeline().addLast(commandHandler).fireChannelRead(packet);
            } else {
                ctx.fireChannelRead(packet);
            }
        } else if (msg instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame());
        } else if (msg instanceof CloseWebSocketFrame) {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("websocket server channelInactive: {}", ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
        log.error("exception message", cause);
        ctx.close();
    }

    private Packet createWebSocketAdapterMessage(WebSocketMessage webSocketMessage) {
        MessageType type = webSocketMessage.getType();
        if (type.equals(MessageType.SIGNALING_OFFER)
                || type.equals(MessageType.SIGNALING_ANSWER)
                || type.equals(MessageType.SIGNALING_CANDIDATE)
                || type.equals(MessageType.SIGNALING_BUSY)
                || type.equals(MessageType.SIGNALING_CLOSE)) {
            return new WebSocketSignalingAdapterMessage(webSocketMessage);
        }
        return new WebSocketAdapterMessage(webSocketMessage);
    }
}
