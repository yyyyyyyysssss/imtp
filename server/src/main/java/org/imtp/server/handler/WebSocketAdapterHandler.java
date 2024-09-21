package org.imtp.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.utils.JsonUtil;
import org.imtp.server.constant.ProjectConstant;
import org.imtp.server.context.ChannelContextHolder;
import org.imtp.server.service.ChatService;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/7 20:14
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketAdapterHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Resource
    private ChatService chatService;

    @Resource
    private TextMessageHandler textMessageHandler;

    @Resource
    private ImageMessageHandler imageMessageHandler;

    @Resource
    private VideoMessageHandler videoMessageHandler;

    @Resource
    private FileMessageHandler fileMessageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof TextWebSocketFrame textWebSocketFrame){
            String text = textWebSocketFrame.text();
            WebSocketMessage webSocketMessage = JsonUtil.parseObject(text, WebSocketMessage.class);
            Packet packet;
            switch (webSocketMessage.getType()){
                case TEXT_MESSAGE :
                    packet = new TextMessage(webSocketMessage);
                    if (ctx.pipeline().get(TextMessageHandler.class) == null){
                        ctx.pipeline().addLast(textMessageHandler).fireChannelRead(packet);
                    }else {
                        ctx.fireChannelRead(packet);
                    }
                    break;
                case IMAGE_MESSAGE:
                    packet = new ImageMessage(webSocketMessage);
                    if (ctx.pipeline().get(ImageMessageHandler.class) == null){
                        ctx.pipeline().addLast(imageMessageHandler).fireChannelRead(packet);
                    }else {
                        ctx.fireChannelRead(packet);
                    }
                    break;
                case VIDEO_MESSAGE:
                    packet = new VideoMessage(webSocketMessage);
                    if (ctx.pipeline().get(VideoMessageHandler.class) == null){
                        ctx.pipeline().addLast(videoMessageHandler).fireChannelRead(packet);
                    }else {
                        ctx.fireChannelRead(packet);
                    }
                    break;
                case FILE_MESSAGE:
                    packet = new FileMessage(webSocketMessage);
                    if (ctx.pipeline().get(FileMessageHandler.class) == null){
                        ctx.pipeline().addLast(fileMessageHandler).fireChannelRead(packet);
                    }else {
                        ctx.fireChannelRead(packet);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported Operation");
            }
        }else if (msg instanceof PingWebSocketFrame){
            ctx.channel().writeAndFlush(new PongWebSocketFrame());
        }else if (msg instanceof CloseWebSocketFrame){
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("websocket server channelInactive: {}",ctx.channel().id().asLongText());
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        channelInactiveHandle(channel);
        log.error("exception message",cause);
        ctx.close();
    }

    private void channelInactiveHandle(Channel channel){
        AttributeKey<String> attributeKey = AttributeKey.valueOf(ProjectConstant.CHANNEL_ATTR_LOGIN_USER);
        String userId = channel.attr(attributeKey).get();
        log.warn("用户[{}]已断开连接",userId);
        //移除用户在线状态
        chatService.userOffline(userId, channel.id().asLongText());
        //移除
        ChannelContextHolder.channelContext().removeChannel(channel.id().asLongText());
    }
}
