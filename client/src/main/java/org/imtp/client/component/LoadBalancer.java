package org.imtp.client.component;

import java.util.List;

public interface LoadBalancer {

    ServiceInfo nextService(List<ServiceInfo> serviceInfos);

}
