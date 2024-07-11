package org.imtp.client;

import org.imtp.client.component.LoadBalancer;
import org.imtp.client.component.LoadBalancerFactory;
import org.imtp.client.component.ServiceInfo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 14:15
 */
public class ClusterServerAddress implements ServerAddress{

    private static volatile ClusterServerAddress clusterServerAddress;

    private static final Lock lock = new ReentrantLock();

    private Config config;

    private LoadBalancer loadBalancer;

    private ClusterServerAddress(){
        this.config = Config.getInstance();
        this.loadBalancer = LoadBalancerFactory.getLoadBalancer();
    }

    public static ClusterServerAddress getInstance(){
        if (clusterServerAddress == null){
            try {
                lock.lock();
                if (clusterServerAddress == null){
                    clusterServerAddress = new ClusterServerAddress();
                }
            }finally {
                lock.unlock();
            }
        }
        return clusterServerAddress;
    }


    @Override
    public ServiceInfo serviceInfo() {
        return new ServiceInfo(config.getHost(),config.getPort());
    }
}