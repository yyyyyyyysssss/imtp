package org.imtp.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.SocketException;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/7 20:14
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if(msg instanceof TextWebSocketFrame textWebSocketFrame){
            String text = textWebSocketFrame.text();
            log.info("TextWebSocketFrame: {}",text);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Hello"));
        }else if (msg instanceof PingWebSocketFrame pingWebSocketFrame){
            log.info("PingWebSocketFrame");
        }else if (msg instanceof CloseWebSocketFrame closeWebSocketFrame){
            log.info("CloseWebSocketFrame");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException){
            log.warn("已断开连接");
        }else {
            log.error("exception message",cause);
        }
        ctx.close();
    }
}
