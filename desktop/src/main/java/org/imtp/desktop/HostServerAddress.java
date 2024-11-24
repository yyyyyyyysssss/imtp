package org.imtp.desktop;

import org.imtp.desktop.component.ServiceInfo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 14:13
 */
public class HostServerAddress implements ServerAddress {

    private static volatile HostServerAddress hostServerAddress;

    private static final Lock lock = new ReentrantLock();

    private Config config;

    private HostServerAddress(){
        config =  Config.getInstance();
    }

    public static HostServerAddress getInstance(){
        if (hostServerAddress == null){
            try {
                lock.lock();
                if (hostServerAddress == null){
                    hostServerAddress = new HostServerAddress();
                }
            }finally {
                lock.unlock();
            }
        }
        return hostServerAddress;
    }

    @Override
    public ServiceInfo serviceInfo() {
        return new ServiceInfo(config.getHost(),config.getPort());
    }

}
