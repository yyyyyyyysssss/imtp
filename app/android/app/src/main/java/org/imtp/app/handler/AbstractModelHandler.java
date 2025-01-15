package org.imtp.app.handler;

import android.util.Log;

import org.imtp.app.NettyClient;
import org.imtp.app.model.MessageModel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractModelHandler<T> extends SimpleChannelInboundHandler<T> {

    private static final String TAG = "AbstractModelHandler";

    protected NettyClient nettyClient;

    protected MessageModel model;

    public AbstractModelHandler(NettyClient nettyClient,MessageModel model){
        this.nettyClient = nettyClient;
        this.model = model;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.w(TAG,"channelInactive");
        this.nettyClient.connect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Log.e(TAG,"exceptionCaught", cause);
        this.nettyClient.connect();
    }

}
