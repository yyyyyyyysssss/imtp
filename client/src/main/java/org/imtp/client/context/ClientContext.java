package org.imtp.client.context;

import io.netty.channel.Channel;
import org.imtp.client.Client;

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

    Client client();
}
