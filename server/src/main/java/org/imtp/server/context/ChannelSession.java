package org.imtp.server.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public interface ChannelSession {

    String id();

    String userId();

    Channel channel();

    boolean isActive();

    ChannelFuture sendMessage(Object msg);

}
