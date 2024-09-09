package org.imtp.server.context;

import java.util.Collection;

public interface ChannelContext {

    void addChannel(String id,ChannelSession channelSession);

    ChannelSession getChannel(String id);

    Collection<ChannelSession> getAllChannel();

    void removeChannel(String id);



}
