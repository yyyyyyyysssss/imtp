package org.imtp.client.context;

import io.netty.channel.Channel;
import org.imtp.client.Client;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:43
 */
public abstract class AbstractClientChannelContext implements ClientContext{

    private Channel channel;

    private Client client;

    public AbstractClientChannelContext(Channel channel,Client client){
        this.channel = channel;
        this.client = client;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public void resetChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Client client() {
        return this.client;
    }
}
