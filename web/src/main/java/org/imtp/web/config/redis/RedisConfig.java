package org.imtp.web.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.imtp.web.config.EmailAuthenticationToken;
import org.imtp.web.config.RefreshAuthenticationToken;
import org.imtp.web.config.RequestUrlAuthority;
import org.imtp.web.config.oauth2.OAuthClientAuthenticationToken;
import org.imtp.web.domain.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.jackson2.WebServletJackson2Module;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/8 16:59
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public <T> RedisTemplate<String,T> authRedisTemplate(RedisConnectionFactory redisConnectionFactory){
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
        objectMapper.addMixIn(RequestUrlAuthority.class, RequestUrlAuthority.RequestUrlAuthorityMixin.class);
        objectMapper.addMixIn(OAuthClientAuthenticationToken.class, OAuthClientAuthenticationToken.OAuthClientAuthenticationTokenMixin.class);
        objectMapper.addMixIn(EmailAuthenticationToken.class, EmailAuthenticationToken.EmailAuthenticationTokenMixin.class);
        objectMapper.addMixIn(RefreshAuthenticationToken.class, RefreshAuthenticationToken.RefreshAuthenticationTokenMixin.class);
        objectMapper.addMixIn(User.class,User.RequestUrlAuthorityMixin.class);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
