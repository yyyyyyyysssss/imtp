package org.imtp.desktop.component;

import org.imtp.desktop.Config;
import org.imtp.desktop.enums.LoadBalancerType;

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
