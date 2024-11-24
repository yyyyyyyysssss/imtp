package org.imtp.desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import org.imtp.desktop.component.LoadBalancer;
import org.imtp.desktop.component.LoadBalancerFactory;
import org.imtp.desktop.component.OKHttpClientHelper;
import org.imtp.desktop.component.ServiceInfo;

import java.util.List;
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

    private OKHttpClientHelper okHttpClientHelper;

    private ClusterServerAddress(){
        this.config = Config.getInstance();
        this.loadBalancer = LoadBalancerFactory.getLoadBalancer();
        this.okHttpClientHelper = OKHttpClientHelper.getInstance();
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
        List<ServiceInfo> serviceInfos = okHttpClientHelper.doGet("/service/discovery", new TypeReference<List<ServiceInfo>>() {});
        return loadBalancer.nextService(serviceInfos);
    }
}
