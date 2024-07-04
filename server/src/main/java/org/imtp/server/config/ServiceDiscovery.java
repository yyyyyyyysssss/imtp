package org.imtp.server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/4 16:16
 */
@Slf4j
public class ServiceDiscovery {

    List<String> serviceInfo;

    private ZooKeeper zooKeeper;

    public ServiceDiscovery(ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
        serviceInfo = new ArrayList<>();
    }

    public void discovery(){
        try {
            List<String> children = zooKeeper.getChildren(ZookeeperConfig.SERVER_REGISTER_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                        discovery();
                    }
                }
            });
            serviceInfo.clear();
            for (String child : children){
                String childPath = ZookeeperConfig.SERVER_REGISTER_PATH + "/" + child;
                byte[] data = zooKeeper.getData(childPath, false, null);
                serviceInfo.add(new String(data));
            }
        } catch (Exception e) {
            log.error("discovery node error:",e);
        }
    }

}
