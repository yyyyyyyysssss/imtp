package org.imtp.web.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.web.config.RedisSecurityContextRepository;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
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
    private RedisSecurityContextRepository securityContextRepository;

    @Resource
    private TokenService tokenService;

    @Resource
    private BearerTokenResolver bearerTokenResolver;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //提取token
        String token = bearerTokenResolver.resolve(request);
        //清除用户的securityContext
        String userId = JwtUtil.extractPayloadInfo(token, PayloadInfo::getSubject);
        securityContextRepository.clearContext(userId);
        //将token过期
        tokenService.revokeToken(token);
    }
}
