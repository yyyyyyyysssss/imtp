package org.imtp.server.context;

import io.netty.channel.Channel;

import java.util.Collection;

public interface ChannelContext {

    void addChannel(String id,ChannelSession channelSession);

    ChannelSession getChannel(String id);

    ChannelSession getChannel(Channel channel);

    Collection<ChannelSession> getAllChannel();

    void removeChannel(String id);



}
