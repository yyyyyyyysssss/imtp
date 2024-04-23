package org.imtp.server.context;

import io.netty.channel.Channel;

public interface ChannelContext {

    void addChannel(String id,Channel channel);

    Channel getChannel(String id);

    void removeChannel(String id);

}
