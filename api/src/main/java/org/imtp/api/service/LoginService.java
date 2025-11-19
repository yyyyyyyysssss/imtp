package org.imtp.api.service;

import jakarta.annotation.Resource;
import org.imtp.api.config.security.SecurityContextStore;
import org.imtp.api.config.security.TokenService;
import org.imtp.api.domain.entity.TokenInfo;
import org.imtp.api.domain.entity.User;
import org.imtp.common.enums.ClientType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/6 11:38
 */
@Service
public class LoginService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    @Resource
    private SecurityContextStore securityContextStore;

    public TokenInfo login(Authentication authenticationToken) {

        return login(authenticationToken, false, ClientType.WEB);
    }

    public TokenInfo login(Authentication authenticationToken, boolean rememberMe, ClientType clientType) {
        Authentication authenticate;
        if ((authenticate = SecurityContextHolder.getContext().getAuthentication()) == null || authenticate instanceof AnonymousAuthenticationToken) {
            authenticate = authenticationManager.authenticate(authenticationToken);
        }
        User user = (User) authenticate.getPrincipal();
        //生成token
        TokenInfo tokenInfo = tokenService.generate(user, clientType, rememberMe);
        String tokenId = tokenInfo.getId();
        user.setTokenId(tokenId);
        //序列化securityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticate);
        securityContextStore.saveContext(securityContext,tokenId);
        return tokenInfo;
    }

}
