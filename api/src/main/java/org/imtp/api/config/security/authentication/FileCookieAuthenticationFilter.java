package org.imtp.api.config.security.authentication;

import groovy.lang.Tuple2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.config.security.RedisSecurityContextRepository;
import org.imtp.api.config.security.TokenService;
import org.imtp.api.config.security.authorization.PathPatternRequestMatcher;
import org.imtp.api.enums.TokenType;
import org.imtp.api.utils.PayloadInfo;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Description OncePerRequestFilter 一次请求中只会执行一次的过滤器
 * @Author ys
 * @Date 2023/7/26 17:30
 */
public class FileCookieAuthenticationFilter extends OncePerRequestFilter {


    private final RequestMatcher tokenEndpointMatcher;

    private final TokenService tokenService;

    public FileCookieAuthenticationFilter(TokenService tokenService){
        this.tokenService = tokenService;
        this.tokenEndpointMatcher = new PathPatternRequestMatcher("/file/**", HttpMethod.GET.name());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.tokenEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null){
            filterChain.doFilter(request,response);
            return;
        }
        String token = extractToken(request);
        if(token != null){
            Tuple2<Boolean, PayloadInfo> valid = tokenService.isValid(token, TokenType.ACCESS_TOKEN);
            if (valid.getV1()){
                String tokenId = valid.getV2().getId();
                request.setAttribute(RedisSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME, tokenId);
            }
        }
        filterChain.doFilter(request,response);
    }

    private String extractToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equalsIgnoreCase(cookie.getName()) || "accessToken".equalsIgnoreCase(cookie.getName()) || "access_token".equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
