package org.imtp.common.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 14:41
 */
@Slf4j
public class ZookeeperHolder {

    private static volatile ZooKeeper zooKeeper;

    private static final Lock lock = new ReentrantLock();

    public static ZooKeeper getZookeeper(String servers){
        return getZookeeper(servers,3000);
    }

    public static ZooKeeper getZookeeper(String servers,Integer sessionTimeout){
        if (zooKeeper == null){
            try {
                lock.lock();
                if (zooKeeper == null){
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    zooKeeper = new ZooKeeper(servers, sessionTimeout, watchedEvent -> {
                        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()){
                            countDownLatch.countDown();
                        }
                    });
                    countDownLatch.await();
                    log.info("初始化zookeeper连接完成：{}",zooKeeper.getState());
                }
            }catch (Exception e){
                log.error("初始化zookeeper异常",e);
            }finally {
                lock.unlock();
            }
        }
        return zooKeeper;
    }

}
