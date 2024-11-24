package org.imtp.client.context;

import io.netty.channel.Channel;
import org.imtp.client.Client;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.packet.body.UserInfo;

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


    public static ClientContext createClientContext(Channel channel,Client client,TokenInfo tokenInfo){

        return createClientContext(channel,client,null,tokenInfo);
    }

    public static ClientContext createClientContext(Channel channel, Client client, UserInfo userInfo, TokenInfo tokenInfo){
        if(clientContext == null){
            try {
                lock.lock();
                if(clientContext == null){
                    clientContext = new DefaultClientUserChannelContext(channel,client,userInfo,tokenInfo);
                }
            }finally {
                lock.unlock();
            }
        }
        return clientContext;
    }

    public static ClientContext clientContext(){
        return clientContext;
    }

}
