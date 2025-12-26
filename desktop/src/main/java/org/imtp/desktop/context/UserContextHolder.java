package org.imtp.desktop.context;

import io.netty.channel.Channel;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.packet.body.UserInfo;
import org.imtp.desktop.Client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2025/12/25 16:54
 */
public class UserContextHolder {

    private static volatile UserContext userContext;

    //使用lock  为后续虚拟线程做准备
    private static final Lock lock = new ReentrantLock();

    public static UserContext createUserContext(UserInfo userInfo){

        return createUserContext(userInfo.getId(),userInfo.getUsername(),userInfo.getNickname(),userInfo.getAvatar());
    }

    public static UserContext createUserContext(Long userId, String username, String nickname, String avatarUrl){
        if(userContext == null){
            try {
                lock.lock();
                if(userContext == null){
                    userContext = new DefaultUserContext(userId,username,nickname,avatarUrl);
                }
            }finally {
                lock.unlock();
            }
        }
        return userContext;
    }

    public static UserContext userContext(){
        if (userContext == null) {
            throw new IllegalStateException("UserContext is not initialized yet.");
        }
        return userContext;
    }

    public static void clearUserContext() {
        userContext = null;
    }


}
