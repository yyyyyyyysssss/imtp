package org.imtp.api.config.security.authentication;

import org.imtp.api.config.redis.RedisHelper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2025/8/29 15:19
 */
public class LoginAttemptService {

    private String prefix = "login:fail:";

    // 窗口时间内最大失败次数
    private final int maxAttempt;

    // 窗口期
    private final long windowMs;

    // 锁定期
    private final long lockMs;

    private final RedisHelper redisHelper;

    // 默认配置：5次失败，15分钟窗口期，30分钟锁定期
    public LoginAttemptService(RedisHelper redisHelper){
        this(redisHelper, 5, 15 * 60 * 1000L, 30 * 60 * 1000L);
    }

    public LoginAttemptService(RedisHelper redisHelper, int maxAttempt, long windowMs, long lockMs) {
        if(redisHelper == null) {
            throw new IllegalArgumentException("redisWrapper must not be null");
        }
        if (maxAttempt <= 0 || windowMs <= 0 || lockMs <= 0) {
            throw new IllegalArgumentException("maxAttempt, windowMs and lockMs must be positive");
        }
        this.redisHelper = redisHelper;
        this.maxAttempt = maxAttempt;
        this.windowMs = windowMs;
        this.lockMs = lockMs;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void loginSucceeded(String username){
        // 登录成功直接删除记录
        redisHelper.delete(key(username));
    }

    public void loginFailed(String username){
        String key = key(username);
        long now = Instant.now().toEpochMilli();
        // 使用zSet存储当前登录失败的时间戳 score = 时间戳 过期时间设为 窗口期+锁定期
        redisHelper.addZSet(key, String.valueOf(now), now, Duration.ofMillis(windowMs + lockMs));
    }

    public boolean isBlocked(String username){
        String key = key(username);
        long now = Instant.now().toEpochMilli();
        // 移除窗口期之前的记录
        redisHelper.removeZSetByScore(key, 0, now - windowMs);
        // 统计窗口期内的失败次数
        Long failCount = redisHelper.countZSet(key);
        if (failCount != null && failCount >= maxAttempt) {
            // 如果超出次数 再判断最早的一次失败是否在锁定期内
            List<Object> timestamps = redisHelper.rangeZSet(key, 0, 0);
            if (timestamps != null && !timestamps.isEmpty()) {
                long firstFailTime  = Long.parseLong((String) timestamps.iterator().next());
                // 如果在锁定期内 则返回true
                if (now - firstFailTime < lockMs) {
                    return true;
                }
            }
        }
        return false;
    }

    private String key(String username) {
        return prefix + username;
    }

}
