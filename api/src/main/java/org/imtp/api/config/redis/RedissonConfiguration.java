package org.imtp.api.config.redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class RedissonConfiguration {

    /**
     * 默认读取 classpath:redisson.yml
     * 服务本身也可以覆盖 redisson.yml
     */
    @Value("${redisson.config:classpath:redisson.yaml}")
    private Resource configFile;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "redisson", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(configFile.getInputStream());
        return Redisson.create(config);
    }

    @Bean
    public DistributedLock distributedLock(RedissonClient redissonClient){

        return new RedissonDistributedLock(redissonClient);
    }

}
