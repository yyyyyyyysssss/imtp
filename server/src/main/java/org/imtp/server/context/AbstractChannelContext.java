package org.imtp.server.context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 13:50
 */
public abstract class AbstractChannelContext implements ChannelContext {

    private Map<String, ChannelSession> channelMap = new ConcurrentHashMap<>();


    protected void init(){

    }

    @Override
    public void addChannel(String id, ChannelSession channel) {
        channelMap.put(id,channel);
    }

    @Override
    public ChannelSession getChannel(String id) {
        return channelMap.get(id);
    }

    @Override
    public Collection<ChannelSession> getAllChannel() {
        return channelMap.values();
    }

    @Override
    public void removeChannel(String id) {
        channelMap.remove(id);
    }
}
