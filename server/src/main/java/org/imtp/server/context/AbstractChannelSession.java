package org.imtp.server.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/8 16:33
 */
public abstract class AbstractChannelSession implements ChannelSession{

    private Channel channel;

    private String userId;

    public AbstractChannelSession(Channel channel,String userId){
        this.channel = channel;
        this.userId = userId;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }


    @Override
    public String userId() {
        return this.userId;
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
