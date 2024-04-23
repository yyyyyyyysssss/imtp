package org.imtp.server.context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 14:14
 */
public class ChannelContextHolder {

    private static ChannelContext channelContext;

    //使用lock  为后续虚拟线程做准备
    private static final Lock lock = new ReentrantLock();

    public static ChannelContext createChannelContext(){
        if(channelContext == null){
            try {
                lock.lock();
                if(channelContext == null){
                    channelContext = new DefaultChannelContext();
                }
            }finally {
                lock.unlock();
            }
        }
        return channelContext;
    }

}
