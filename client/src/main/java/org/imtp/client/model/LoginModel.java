package org.imtp.client.model;

import io.netty.channel.ChannelHandler;

public interface LoginModel extends Model<Void>{

    void ready(ChannelHandler channelHandler);

}
