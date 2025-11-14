package org.imtp.api.config.security.authentication.refreshtoken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/25 15:25
 */
public class RefreshTokenAuthenticationFilter extends OncePerRequestFilter {

    private BearerTokenResolver bearerTokenResolver;

    private RefreshTokenServices tokenService;

    private AuthenticationManager authenticationManager;

    private final RequestMatcher tokenEndpointMatcher;

    public RefreshTokenAuthenticationFilter(AuthenticationManager authenticationManager,BearerTokenResolver bearerTokenResolver, RefreshTokenServices refreshTokenServices){
        this.authenticationManager = authenticationManager;
        this.bearerTokenResolver = bearerTokenResolver;
        this.tokenService = refreshTokenServices;
        this.tokenEndpointMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET,"/refreshToken");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext != null && securityContext.getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }
        if (!this.tokenEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = bearerTokenResolver.resolve(request);
        if (token == null){
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = tokenService.refreshToken(token);
        if(authentication != null && securityContext != null){
            Authentication authenticate = authenticationManager.authenticate(authentication);
            securityContext.setAuthentication(authenticate);
        }
        filterChain.doFilter(request,response);
    }
}
