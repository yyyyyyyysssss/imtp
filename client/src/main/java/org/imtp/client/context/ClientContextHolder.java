package org.imtp.client.context;

import io.netty.channel.Channel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:47
 */
public class ClientContextHolder {

    private static volatile ClientContext clientContext;

    //使用lock  为后续虚拟线程做准备
    private static final Lock lock = new ReentrantLock();

    public static ClientContext createClientContext(Channel channel,String principal,String credentials){
        if(clientContext == null){
            try {
                lock.lock();
                if(clientContext == null){
                    clientContext = new DefaultClientUserChannelContext(channel,principal,credentials);
                }
            }finally {
                lock.unlock();
            }
        }
        return clientContext;
    }

    public static ClientContext clientContext(){
        if(clientContext == null){
            throw new RuntimeException("ClientContext未初始化");
        }
        return clientContext;
    }

}
