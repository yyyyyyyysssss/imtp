package org.imtp.client.context;

import io.netty.channel.Channel;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:45
 */
public class DefaultClientUserChannelContext extends AbstractClientChannelContext {

    private UserInfo userInfo;

    public DefaultClientUserChannelContext(Channel channel, UserInfo userInfo) {
        super(channel);
        this.userInfo = userInfo;
    }


    @Override
    public Long id() {
        return this.userInfo.getId();
    }

    @Override
    public String principal() {
        return this.userInfo.getUsername();
    }

    @Override
    public String credentials() {
        return this.userInfo.getPassword();
    }
}
