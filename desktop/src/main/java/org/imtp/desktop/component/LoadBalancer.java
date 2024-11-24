package org.imtp.desktop.component;

import java.util.List;

public interface LoadBalancer {

    ServiceInfo nextService(List<ServiceInfo> serviceInfos);

}
