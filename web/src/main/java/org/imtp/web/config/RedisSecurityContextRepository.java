package org.imtp.web.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.time.Duration;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/17 15:16
 */
public class RedisSecurityContextRepository implements SecurityContextRepository {

    public static final String DEFAULT_REQUEST_ATTR_NAME = "IM_SECURITY_CONTEXT";

    private static final String SECURITY_CONTEXT_KEY_PREFIX = "security:context:repository:";

    @Resource
    private RedisTemplate<String, SecurityContext> authRedisTemplate;

    @Resource
    private AuthProperties authProperties;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        String attribute = (String)request.getAttribute(DEFAULT_REQUEST_ATTR_NAME);
        if (attribute == null || attribute.isEmpty()){
            return null;
        }
        try {
            return authRedisTemplate.opsForValue().get(SECURITY_CONTEXT_KEY_PREFIX + attribute);
        }finally {
            request.removeAttribute(DEFAULT_REQUEST_ATTR_NAME);
        }
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String attribute = (String)request.getAttribute(DEFAULT_REQUEST_ATTR_NAME);
        if (attribute == null || attribute.isEmpty()){
            return;
        }
        try {
            // 如果当前的context是空的，则移除
            SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
            if (emptyContext.equals(context)){
                authRedisTemplate.delete(SECURITY_CONTEXT_KEY_PREFIX + attribute);
            }else {
                Long expiration = authProperties.getJwt().getExpiration();
                authRedisTemplate.opsForValue().set(SECURITY_CONTEXT_KEY_PREFIX + attribute,context, Duration.ofSeconds(expiration));
            }
        }finally {
            request.removeAttribute(DEFAULT_REQUEST_ATTR_NAME);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String attribute = (String)request.getAttribute(DEFAULT_REQUEST_ATTR_NAME);
        if (attribute == null || attribute.isEmpty()){
            return false;
        }
        return Boolean.TRUE.equals(authRedisTemplate.hasKey(SECURITY_CONTEXT_KEY_PREFIX + attribute));
    }

    public boolean refreshContextExpiration(String userId){
        Long expiration = authProperties.getJwt().getExpiration();
        return Boolean.TRUE.equals(authRedisTemplate.expire(SECURITY_CONTEXT_KEY_PREFIX + userId,Duration.ofSeconds(expiration)));
    }


    public boolean clearContext(String userId){

        return Boolean.TRUE.equals(authRedisTemplate.delete(SECURITY_CONTEXT_KEY_PREFIX + userId));
    }

}
