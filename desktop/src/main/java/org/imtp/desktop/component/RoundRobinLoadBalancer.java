package org.imtp.desktop.component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 轮询
 * @Author ys
 * @Date 2024/7/5 15:36
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceInfo nextService(List<ServiceInfo> serviceInfos) {
        int index = currentIndex.getAndIncrement();
        index = index % serviceInfos.size();
        return serviceInfos.get(index);
    }


}
