package org.imtp.api.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/17 15:16
 */
public class RedisSecurityContextRepository implements SecurityContextStore {

    public static final String DEFAULT_REQUEST_ATTR_NAME = "IM_SECURITY_CONTEXT";

    private static final String SECURITY_CONTEXT_KEY_PREFIX = "security:context:repository:";

    private RedisTemplate<String,SecurityContext> redisTemplate;

    private SecurityProperties securityProperties;

    public RedisSecurityContextRepository(RedisTemplate<String,SecurityContext> redisTemplate, SecurityProperties securityProperties){
        this.redisTemplate = redisTemplate;
        this.securityProperties = securityProperties;
    }

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        String attribute = (String)request.getAttribute(DEFAULT_REQUEST_ATTR_NAME);
        if (attribute == null || attribute.isEmpty()){
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(SECURITY_CONTEXT_KEY_PREFIX + attribute);
        }finally {
            request.removeAttribute(DEFAULT_REQUEST_ATTR_NAME);
        }
    }

    @Override
    public void saveContext(SecurityContext context, String tokenId) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        request.setAttribute(DEFAULT_REQUEST_ATTR_NAME, tokenId);
        saveContext(context,request,response);
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
                redisTemplate.delete(SECURITY_CONTEXT_KEY_PREFIX + attribute);
            }else {
                Long expiration = securityProperties.getJwt().getExpiration();
                redisTemplate.opsForValue().set(SECURITY_CONTEXT_KEY_PREFIX + attribute,context, Duration.ofSeconds(expiration));
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
        return Boolean.TRUE.equals(redisTemplate.hasKey(SECURITY_CONTEXT_KEY_PREFIX + attribute));
    }


    @Override
    public boolean clearContext(String tokenId){

        return Boolean.TRUE.equals(redisTemplate.delete(SECURITY_CONTEXT_KEY_PREFIX + tokenId));
    }

}
