package org.imtp.api.config.redis;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.StatefulConnection;
import jakarta.annotation.Resource;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.imtp.api.config.security.authentication.email.EmailAuthenticationToken;
import org.imtp.api.config.security.authentication.ott.OneTimeTokenAuthenticationTokenMixin;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshAuthenticationToken;
import org.imtp.api.config.security.oauth2.OAuthClientAuthenticationToken;
import org.imtp.api.context.TenantContext;
import org.imtp.api.domain.entity.AuthorityUrl;
import org.imtp.api.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.jackson2.WebServletJackson2Module;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 16:59
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig{

    @Value("${spring.application.name:''}")
    private String applicationName;

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisConfiguration redisConfig;
        //  哨兵模式
        if (redisProperties.getSentinel() != null && !redisProperties.getSentinel().getNodes().isEmpty()) {
            redisConfig = createSentinelConfig();
        } else if(redisProperties.getCluster() != null && !redisProperties.getCluster().getNodes().isEmpty()){
            // 集群模式
            redisConfig = createClusterConfig();
        } else {
            // 单机模式
            redisConfig = createStandaloneConfig();
        }

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration
                .builder()
                .poolConfig(createPoolConfig())
                .build();
        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    // 单机配置
    private RedisStandaloneConfiguration createStandaloneConfig(){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        if (redisProperties.getPassword() != null) {
            config.setPassword(redisProperties.getPassword());
        }
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    // 哨兵配置
    private RedisSentinelConfiguration createSentinelConfig() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        Set<String> nodeSet = new HashSet<>(sentinel.getNodes());
        RedisSentinelConfiguration config = new RedisSentinelConfiguration(sentinel.getMaster(), nodeSet);
        if (redisProperties.getPassword() != null) {
            config.setPassword(redisProperties.getPassword());
        }
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    // 集群配置
    private RedisClusterConfiguration createClusterConfig() {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
        if (redisProperties.getPassword() != null) {
            config.setPassword(redisProperties.getPassword());
        }
        return config;
    }

    private GenericObjectPoolConfig<StatefulConnection<?, ?>> createPoolConfig() {
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        RedisProperties.Pool pool = redisProperties.getLettuce().getPool();
        if (pool != null) {
            poolConfig.setMaxIdle(pool.getMaxIdle());
            poolConfig.setMinIdle(pool.getMinIdle());
            poolConfig.setMaxTotal(pool.getMaxActive());
            poolConfig.setMaxWait(pool.getMaxWait());
        }
        return poolConfig;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setDefaultSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {

        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisHelper redisHelper(RedisTemplate<String, Object> redisTemplate,GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer,ObjectMapper objectMapper) {
        return new RedisHelper(redisTemplate,genericJackson2JsonRedisSerializer,objectMapper);
    }

    @Bean
    public <T> RedisTemplate<String, T> authRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.setValueSerializer(authRedisSerializer());
        redisTemplate.setHashValueSerializer(authRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisSerializer<Object> authRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new CoreJackson2Module());
        objectMapper.registerModule(new WebServletJackson2Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.addMixIn(RequestUrlAuthority.class, RequestUrlAuthority.RequestUrlAuthorityMixin.class);
        objectMapper.addMixIn(AuthorityUrl.class, AuthorityUrl.AuthorityUrlMixin.class);
        objectMapper.addMixIn(OAuthClientAuthenticationToken.class, OAuthClientAuthenticationToken.OAuthClientAuthenticationTokenMixin.class);
        objectMapper.addMixIn(EmailAuthenticationToken.class, EmailAuthenticationToken.EmailAuthenticationTokenMixin.class);
        objectMapper.addMixIn(RefreshAuthenticationToken.class, RefreshAuthenticationToken.RefreshAuthenticationTokenMixin.class);
        objectMapper.addMixIn(OneTimeTokenAuthenticationToken.class, OneTimeTokenAuthenticationTokenMixin.class);
        objectMapper.addMixIn(User.class, User.UserMixin.class);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration(Duration.ofHours(1)))
                .build();

    }


    public RedisCacheConfiguration redisCacheConfiguration(Duration duration) {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        genericJackson2JsonRedisSerializer.configure(objectMapper -> {
            objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        });
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(duration) // 默认1小时过期
                .computePrefixWith(cacheName -> {
                    Long tenantId = TenantContext.getTenantId();
                    return applicationName + ":" + tenantId + ":" + cacheName + ":";
                })
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer))
                .disableCachingNullValues();// 关闭缓存null
    }

}
