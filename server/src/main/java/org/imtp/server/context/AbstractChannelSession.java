package org.imtp.server.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/8 16:33
 */
public abstract class AbstractChannelSession implements ChannelSession{

    private Channel channel;

    public AbstractChannelSession(Channel channel){
        this.channel = channel;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }


    @Override
    public String id() {
        return this.channel.id().asLongText();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }
}
