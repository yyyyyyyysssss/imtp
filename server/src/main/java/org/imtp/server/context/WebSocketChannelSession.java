package org.imtp.server.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.imtp.common.utils.JsonUtil;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/8 16:38
 */
public class WebSocketChannelSession extends AbstractChannelSession{
    public WebSocketChannelSession(Channel channel,String userId) {
        super(channel,userId);
    }

    @Override
    public ChannelFuture sendMessage(Object msg) {
        return this.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.toJSONString(msg)));
    }
}
