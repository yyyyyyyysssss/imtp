package org.imtp.client.context;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:11
 */
public interface ClientContext {

    Channel channel();

    void setChannel(Channel channel);

    String user();

}
