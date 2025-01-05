package org.imtp.app.handler;

import org.imtp.app.model.MessageModel;

import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractModelHandler<T> extends SimpleChannelInboundHandler<T> {

    protected MessageModel model;

    public AbstractModelHandler(MessageModel model){
        this.model = model;
    }


}
