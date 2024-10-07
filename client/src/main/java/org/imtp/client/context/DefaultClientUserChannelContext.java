package org.imtp.client.context;

import io.netty.channel.Channel;
import org.imtp.client.Client;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.packet.body.UserInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:45
 */
public class DefaultClientUserChannelContext extends AbstractClientChannelContext {

    private UserInfo userInfo;

    private TokenInfo tokenInfo;

    public DefaultClientUserChannelContext(Channel channel, Client client, UserInfo userInfo,TokenInfo tokenInfo) {
        super(channel,client);
        this.userInfo = userInfo;
        this.tokenInfo = tokenInfo;
    }


    @Override
    public Long id() {
        return this.userInfo.getId();
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }


    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }
}
