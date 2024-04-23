package org.imtp.client.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:45
 */
public class DefaultClientUserChannelContext extends AbstractClientChannelContext {

    private String user;

    public DefaultClientUserChannelContext(Channel channel, String user) {
        super(channel);
        this.user = user;
    }

    @Override
    public String user() {
        return this.user;
    }
}
