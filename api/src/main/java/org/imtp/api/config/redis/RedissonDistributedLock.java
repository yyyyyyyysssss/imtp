package org.imtp.api.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonDistributedLock implements DistributedLock{

    private RedissonClient redissonClient;

    public RedissonDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RLock lock(String key) {
        RLock lock = getLock(key);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String key, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(key);
        lock.lock(leaseTime, unit); // 不使用看门狗
        return lock;
    }

    @Override
    public RLock tryLock(String key) {
        try {
            RLock lock = getLock(key);
            return lock.tryLock() ? lock : null;
        } catch (Exception e) {
            log.error("tryLock error key={}", key, e);
            return null;
        }
    }

    @Override
    public RLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            RLock lock = redissonClient.getLock(key);

            boolean success;
            if (leaseTime <= 0) {
                success = lock.tryLock(waitTime, unit);
            } else {
                success = lock.tryLock(waitTime, leaseTime, unit);
            }
            return success ? lock : null;
        } catch (Exception e) {
            log.error("tryLock error key={}", key, e);
            return null;
        }
    }

    @Override
    public LockHandle lockAuto(String key) {
        RLock lock = lock(key);
        return lock::unlock;
    }

    @Override
    public LockHandle lockAuto(String key, long leaseTime, TimeUnit unit) {
        RLock lock = lock(key, leaseTime, unit);
        return lock::unlock;
    }

    @Override
    public LockHandle tryLockAuto(String key, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = tryLock(key, waitTime, leaseTime, unit);
        if (lock == null) return null;
        return lock::unlock;
    }

    private RLock getLock(String key) {
        return redissonClient.getLock(key);
    }
}
