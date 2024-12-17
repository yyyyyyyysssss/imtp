package org.imtp.app.context;

import io.netty.channel.Channel;

import org.imtp.app.NettyClient;
import org.imtp.common.packet.body.TokenInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:43
 */
public abstract class AbstractClientChannelContext implements ClientContext{

    private Channel channel;

    private TokenInfo tokenInfo;

    public AbstractClientChannelContext(Channel channel,TokenInfo tokenInfo){
        this.channel = channel;
        this.tokenInfo = tokenInfo;
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
    public TokenInfo tokenInfo() {
        return this.tokenInfo;
    }
}
