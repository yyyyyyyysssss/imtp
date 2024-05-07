package org.imtp.client.model;

import io.netty.channel.ChannelHandler;

public interface LoginModel extends Model{

    void ready(ChannelHandler channelHandler);

}
