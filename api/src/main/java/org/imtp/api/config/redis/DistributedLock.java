package org.imtp.api.config.redis;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    /**
     * 加锁（阻塞直到成功）
     */
    RLock lock(String key);

    /**
     * 加锁（带租期，不使用看门狗）
     */
    RLock lock(String key, long leaseTime, TimeUnit unit);

    /**
     * 尝试获取锁（立即返回）
     */
    RLock tryLock(String key);

    /**
     * 尝试在指定时间内获取锁（可指定租期）
     * leaseTime = -1 时使用 Redisson 看门狗
     */
    RLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 使用 try-with-resources 自动释放锁
     */
    LockHandle lockAuto(String key);

    LockHandle lockAuto(String key, long leaseTime, TimeUnit unit);

    LockHandle tryLockAuto(String key, long waitTime, long leaseTime, TimeUnit unit);

    default LockHandle tryLockAuto(String key) {
        return tryLockAuto(key, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * AutoCloseable 自动释放
     */
    interface LockHandle extends AutoCloseable {
        @Override
        void close();
    }

}
