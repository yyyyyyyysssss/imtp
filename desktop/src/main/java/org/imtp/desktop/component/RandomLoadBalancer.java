package org.imtp.desktop.component;

import java.util.List;
import java.util.Random;

/**
 * @Description 随机
 * @Author ys
 * @Date 2024/7/5 15:42
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public ServiceInfo nextService(List<ServiceInfo> serviceInfos) {
        if (serviceInfos.isEmpty()){
            return null;
        }
        int index = new Random().nextInt(serviceInfos.size());
        return serviceInfos.get(index);
    }
}
