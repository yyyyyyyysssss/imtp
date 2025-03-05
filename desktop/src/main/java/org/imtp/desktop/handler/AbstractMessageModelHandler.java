package org.imtp.desktop.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.base.Packet;
import org.imtp.desktop.Client;
import org.imtp.desktop.constant.SendMessageListener;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.model.MessageModel;
import org.imtp.desktop.model.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 消息发布
 * @Author ys
 * @Date 2024/4/26 11:41
 */
@Slf4j
public abstract class AbstractMessageModelHandler<T> extends SimpleChannelInboundHandler<T> implements MessageModel {

    private final Lock lock;

    private List<Observer> observers;

    public AbstractMessageModelHandler(){
        observers = new ArrayList<>();
        lock = new ReentrantLock();
    }


    //有消息到达，则发布消息
    @Override
    public void publishMessage(Packet packet) {
        this.notifyObservers(packet);
    }

    @Override
    public void sendMessage(Packet packet) {
        sendMessage(packet,null);
    }

    @Override
    public void sendMessage(Packet packet, SendMessageListener sendMessageListener) {
        if (ClientContextHolder.clientContext() == null){
            if (sendMessageListener != null){
                sendMessageListener.isFail();
            }
            return;
        }
        ChannelFuture channelFuture = ClientContextHolder.clientContext().channel().writeAndFlush(packet);
        if (sendMessageListener != null){
            channelFuture.addListener((ChannelFutureListener) cf -> {
                if(cf.isSuccess()){
                    sendMessageListener.isSuccess();
                }else {
                    sendMessageListener.isFail();
                }
            });
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        if(observer == null){
            throw new NullPointerException();
        }
        try {
            lock.lock();
            if(!observers.contains(observer)){
                observers.add(observer);
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observers.isEmpty()){
            return;
        }
        try {
            lock.lock();
            observers.remove(observer);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void removeObserver(Class<? extends Observer> observerClass) {
        if (observers.isEmpty()){
            return;
        }
        try {
            lock.lock();
            Iterator<Observer> iterator = observers.iterator();
            while (iterator.hasNext()){
                Observer observer = iterator.next();
                if (observerClass.isInstance(observer)){
                    iterator.remove();
                }
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyObservers(Object object) {
        Object[] arrLocal;
        try {
            lock.lock();
            arrLocal = observers.toArray();
            for (int i = arrLocal.length-1; i >= 0 ; i--) {
                ((Observer)arrLocal[i]).update(object);
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Client client = ClientContextHolder.clientContext().client();
        client.resetChannelHandler(this);
        client.connect();
    }
}
