package org.imtp.app.context;

import org.imtp.app.NettyClient;
import org.imtp.app.model.MessageModel;
import org.imtp.common.packet.body.TokenInfo;

import io.netty.channel.Channel;

public class DefaultClientContext extends AbstractClientChannelContext{

    public DefaultClientContext(Channel channel, TokenInfo tokenInfo) {
        super(channel, tokenInfo);
    }

    @Override
    public Long id() {
        return 0L;
    }
}
