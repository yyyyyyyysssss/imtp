package org.imtp.server.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;


/**
 * @Description
 * @Author ys
 * @Date 2024/9/8 16:37
 */
public class IMChannelSession extends AbstractChannelSession {
    public IMChannelSession(Channel channel) {
        super(channel);
    }

    @Override
    public ChannelFuture sendMessage(Object msg) {
        return this.channel().writeAndFlush(msg);
    }
}
