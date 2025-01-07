package org.imtp.app.handler;

import org.imtp.app.NettyClient;
import org.imtp.app.model.MessageModel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractModelHandler<T> extends SimpleChannelInboundHandler<T> {

    protected NettyClient nettyClient;

    protected MessageModel model;

    public AbstractModelHandler(NettyClient nettyClient,MessageModel model){
        this.nettyClient = nettyClient;
        this.model = model;
    }
}
