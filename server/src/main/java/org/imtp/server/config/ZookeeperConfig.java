package org.imtp.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.imtp.common.component.ZookeeperHolder;
import org.imtp.common.component.ZookeeperMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/4 11:52
 */

@Component
@ConfigurationProperties(prefix = "im.server.configuration.zookeeper")
@ConditionalOnProperty(name = "im.server.configuration.model", havingValue = "cluster")
@Getter
@Setter
@Slf4j
public class ZookeeperConfig {

    private String servers;

    private Integer sessionTimeout;

    @Bean
    public ZooKeeper zooKeeper(){
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = ZookeeperHolder.getZookeeper(servers,sessionTimeout);
            //创建服务注册永久节点
            if (zooKeeper.exists(ZookeeperMetadata.SERVER_REGISTER_PATH,false) == null){
                zooKeeper.create(ZookeeperMetadata.SERVER_REGISTER_PATH,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }catch (Exception e){
            log.error("初始化zookeeper异常",e);
        }
        return zooKeeper;
    }

    @DependsOn("zooKeeper")
    @Bean
    public ServiceRegister serviceRegister(ZooKeeper zooKeeper){
        return new ServiceRegister(zooKeeper);
    }

}
