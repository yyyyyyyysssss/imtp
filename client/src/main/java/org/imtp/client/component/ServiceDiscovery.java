package org.imtp.client.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.imtp.common.component.ZookeeperMetadata;
import org.imtp.common.utils.JsonUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/4 16:16
 */
@Slf4j
public class ServiceDiscovery {

    private List<ServiceInfo> serviceInfos;

    private ZooKeeper zooKeeper;

    public ServiceDiscovery(ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
        this.serviceInfos = new CopyOnWriteArrayList<>();
        //服务发现
        discovery();
    }

    public void discovery(){
        try {
            List<String> children = zooKeeper.getChildren(ZookeeperMetadata.SERVER_REGISTER_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                        discovery();
                    }
                }
            });
            serviceInfos.clear();
            for (String child : children){
                String childPath = ZookeeperMetadata.SERVER_REGISTER_PATH + "/" + child;
                byte[] data = zooKeeper.getData(childPath, false, null);
                ServiceInfo serviceInfo = JsonUtil.parseObject(data, ServiceInfo.class);
                serviceInfos.add(serviceInfo);
            }
        } catch (Exception e) {
            log.error("discovery node error:",e);
        }
    }

    public List<ServiceInfo> getServiceInfos(){
        return serviceInfos;
    }

}
