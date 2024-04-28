package org.imtp.client.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:43
 */
public abstract class AbstractClientChannelContext implements ClientContext{

    private Long id;

    private Channel channel;

    public AbstractClientChannelContext(Channel channel){
        this.channel = channel;
    }

    @Override
    public Long id() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }


    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
