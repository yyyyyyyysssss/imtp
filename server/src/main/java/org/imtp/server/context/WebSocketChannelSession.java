package org.imtp.server.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/8 16:38
 */
public class WebSocketChannelSession extends AbstractChannelSession{
    public WebSocketChannelSession(Channel channel) {
        super(channel);
    }

    @Override
    public ChannelFuture sendMessage(Object msg) {
        return this.channel().writeAndFlush(new TextWebSocketFrame((String) msg));
    }
}
