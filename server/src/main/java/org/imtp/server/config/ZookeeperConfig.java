package org.imtp.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

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

    public static final String SERVER_REGISTER_PATH = "/im_server";

    private String servers;

    private Integer sessionTimeout;

    @Bean
    public ZooKeeper zooKeeper(){
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(servers, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (Event.KeeperState.SyncConnected == watchedEvent.getState()){
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            //创建服务注册永久节点
            if (zooKeeper.exists(SERVER_REGISTER_PATH,false) == null){
                zooKeeper.create(SERVER_REGISTER_PATH,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            log.info("初始化zookeeper连接：{}",zooKeeper.getState());
        }catch (Exception e){
            log.error("初始化zookeeper异常",e);
        }
        return zooKeeper;
    }

    @Bean
    public ServiceRegister serviceRegister(ZooKeeper zooKeeper){
        return new ServiceRegister(zooKeeper);
    }

}
