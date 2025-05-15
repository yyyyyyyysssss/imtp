package org.imtp.api.config.security.authentication;

import groovy.lang.Tuple2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.config.security.RedisSecurityContextRepository;
import org.imtp.api.enums.TokenType;
import org.imtp.api.config.security.TokenService;
import org.imtp.api.utils.PayloadInfo;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Description OncePerRequestFilter 一次请求中只会执行一次的过滤器
 * @Author ys
 * @Date 2023/7/26 17:30
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private BearerTokenResolver bearerTokenResolver;

    private TokenService tokenService;

    public TokenAuthenticationFilter(BearerTokenResolver bearerTokenResolver,TokenService tokenService){
        this.bearerTokenResolver = bearerTokenResolver;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //登录接口或已授权的接口直接放行
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (request.getServletPath().contains("/login") || request.getServletPath().contains("/refreshToken") || (securityContext != null && securityContext.getAuthentication() != null)){
            filterChain.doFilter(request,response);
            return;
        }
        String token = bearerTokenResolver.resolve(request);
        if (token == null){
            filterChain.doFilter(request, response);
            return;
        }
        //设置请求属性  由RedisSecurityContextRepository加载SecurityContext
        Tuple2<Boolean, PayloadInfo> valid = tokenService.isValid(token, TokenType.ACCESS_TOKEN);
        if (valid.getV1()){
            String tokenId = valid.getV2().getId();
            request.setAttribute(RedisSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME, tokenId);
        }
        filterChain.doFilter(request,response);
    }
}
