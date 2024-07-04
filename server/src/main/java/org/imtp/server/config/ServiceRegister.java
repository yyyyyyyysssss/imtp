package org.imtp.server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/4 16:12
 */
@Slf4j
public class ServiceRegister {

    private ZooKeeper zooKeeper;

    public ServiceRegister(ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
    }

    //注册服务
    public boolean register(String nodeName,String data){
        try {
            String path = ZookeeperConfig.SERVER_REGISTER_PATH + "/" + nodeName;
            zooKeeper.create(path,data.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
            log.error("register node error:",e);
            return false;
        }
    }

}
