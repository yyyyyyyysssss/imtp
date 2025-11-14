package org.imtp.api.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.config.security.TokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/27 10:52
 */
@Service
public class LogoutService implements LogoutHandler {

    @Resource
    @Lazy
    private BearerTokenResolver bearerTokenResolver;

    @Resource
    @Lazy
    private TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //提取token
        String token = bearerTokenResolver.resolve(request);
        //将token过期
        tokenService.revokeToken(token);
    }
}
