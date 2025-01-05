package org.imtp.app.model;

import org.imtp.app.context.ClientContext;
import org.imtp.app.context.ClientContextHolder;
import org.imtp.common.packet.base.Packet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class MessageModel extends AbstractModel{

    public void publishMessage(Packet packet){
        this.notifyObservers(packet);
    }

    public void sendMessage(Packet packet){
        sendMessage(packet,null);
    }

    public void sendMessage(Packet packet, MessageListenerListener messageListenerListener){
        ClientContext clientContext = ClientContextHolder.clientContext();
        if (clientContext == null){
            if (messageListenerListener != null){
                messageListenerListener.exception(new NullPointerException("ClientContext Uninitialized"));
            }
            return;
        }
        ChannelFuture channelFuture = clientContext.channel().writeAndFlush(packet);
        if (messageListenerListener != null){
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()){
                    messageListenerListener.succeed();
                }else {
                    messageListenerListener.exception(future.cause());
                }
            });
        }
    }

}
