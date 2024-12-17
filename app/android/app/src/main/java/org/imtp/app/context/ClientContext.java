package org.imtp.app.context;

import org.imtp.app.NettyClient;
import org.imtp.common.packet.body.TokenInfo;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:11
 */
public interface ClientContext {

    //客户端登录用户唯一标识
    Long id();

    Channel channel();

    void resetChannel(Channel channel);

    TokenInfo tokenInfo();
}
