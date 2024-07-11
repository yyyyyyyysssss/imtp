package org.imtp.gateway.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
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
@ConfigurationProperties(prefix = "zookeeper")
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
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(servers, sessionTimeout, watchedEvent -> {
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            log.info("初始化zookeeper连接完成：{}",zooKeeper.getState());
        }catch (Exception e){
            log.error("初始化zookeeper异常",e);
        }
        return zooKeeper;
    }


    @Bean
    public ServiceDiscovery serviceDiscovery(ZooKeeper zooKeeper){

        return new ServiceDiscovery(zooKeeper);
    }


}
