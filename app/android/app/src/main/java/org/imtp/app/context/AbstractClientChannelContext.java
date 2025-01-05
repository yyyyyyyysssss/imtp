package org.imtp.app.context;

import org.imtp.common.packet.body.TokenInfo;

import io.netty.channel.Channel;

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
    public TokenInfo tokenInfo() {
        return this.tokenInfo;
    }


    @Override
    public void reset(Channel channel, TokenInfo tokenInfo) {
        this.channel = channel;
        this.tokenInfo = tokenInfo;
    }


}
