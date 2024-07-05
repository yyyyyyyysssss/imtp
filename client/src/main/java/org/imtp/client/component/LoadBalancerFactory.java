package org.imtp.client.component;

import org.imtp.client.Config;
import org.imtp.client.enums.LoadBalancerType;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 16:13
 */
public class LoadBalancerFactory {


    public static LoadBalancer getLoadBalancer(){
        LoadBalancerType loadBalancerType = Config.getInstance().getLoadBalancerType();
        return getLoadBalancer(loadBalancerType);
    }

    public static LoadBalancer getLoadBalancer(LoadBalancerType loadBalancerType){
        switch (loadBalancerType){
            case ROUND :
                return new RoundRobinLoadBalancer();
            case RANDOM:
                return new RandomLoadBalancer();
            default:
                throw new UnsupportedOperationException("未知的操作类型：" + loadBalancerType);
        }
    }

}
