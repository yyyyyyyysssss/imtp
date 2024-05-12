package org.imtp.client.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.model.Observer;
import org.imtp.client.model.MessageModel;
import org.imtp.common.packet.base.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 消息发布
 * @Author ys
 * @Date 2024/4/26 11:41
 */
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
        ClientContextHolder.clientContext().channel().writeAndFlush(packet);
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
        try {
            lock.lock();
            observers.remove(observer);
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
    public void pullFriendship() {
        throw new UnsupportedOperationException("pullFriendship");
    }

    @Override
    public void pullGroupRelationship() {
        throw new UnsupportedOperationException("pullFriendship");
    }

    @Override
    public void pullOfflineMessage() {
        throw new UnsupportedOperationException("pullFriendship");
    }
}
