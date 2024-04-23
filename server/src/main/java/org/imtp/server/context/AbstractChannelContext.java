package org.imtp.server.context;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 13:50
 */
public abstract class AbstractChannelContext implements ChannelContext {

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>(1000);


    protected void init(){

    }

    @Override
    public void addChannel(String id, Channel channel) {
        channelMap.put(id,channel);
    }

    @Override
    public Channel getChannel(String id) {
        return channelMap.get(id);
    }

    @Override
    public void removeChannel(String id) {
        channelMap.remove(id);
    }

    private void checkChannel(Channel channel){
        if(channel == null || !channel.isActive()){
            throw new RuntimeException("channel is inactive or empty");
        }
    }
}
