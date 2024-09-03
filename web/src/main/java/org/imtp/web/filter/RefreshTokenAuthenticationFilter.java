package org.imtp.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.web.config.RedisSecurityContextRepository;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/25 15:25
 */
public class RefreshTokenAuthenticationFilter extends OncePerRequestFilter {

    private BearerTokenResolver bearerTokenResolver;

    private TokenService tokenService;

    private final RequestMatcher tokenEndpointMatcher;

    public RefreshTokenAuthenticationFilter(BearerTokenResolver bearerTokenResolver,TokenService tokenService){
        this.bearerTokenResolver = bearerTokenResolver;
        this.tokenService = tokenService;
        this.tokenEndpointMatcher = new AntPathRequestMatcher("/refreshToken", HttpMethod.GET.name());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (!this.tokenEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = bearerTokenResolver.resolve(request);
        if (token == null){
            filterChain.doFilter(request, response);
            return;
        }
        //设置请求属性  由RedisSecurityContextRepository加载SecurityContext
        if (securityContext != null) {
            if (tokenService.isValid(token,TokenType.REFRESH_TOKEN)) {
                GrantedAuthority grantedAuthority = () -> "refresh_token";
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null, Collections.singleton(grantedAuthority));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
