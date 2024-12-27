package org.imtp.app.context;

import org.imtp.common.packet.body.TokenInfo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.Channel;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 15:47
 */
public class ClientContextHolder {

    private static volatile ClientContext clientContext;

    //使用lock  为后续虚拟线程做准备
    private static final Lock lock = new ReentrantLock();


    public static ClientContext createClientContext(Channel channel, TokenInfo tokenInfo){
        if(clientContext == null){
            try {
                lock.lock();
                if(clientContext == null){
                    clientContext = new DefaultClientContext(channel,tokenInfo);
                }
            }finally {
                lock.unlock();
            }
        }else {
            clientContext.resetTokenInfo(tokenInfo);
        }
        return clientContext;
    }

    public static ClientContext clientContext(){
        return clientContext;
    }

}
