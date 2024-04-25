package org.imtp.client.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:45
 */
public class DefaultClientUserChannelContext extends AbstractClientChannelContext {

    private String principal;

    private String credentials;

    public DefaultClientUserChannelContext(Channel channel, String principal,String credentials) {
        super(channel);
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public String user() {
        return this.principal;
    }

    @Override
    public String credentials() {
        return this.credentials;
    }
}
