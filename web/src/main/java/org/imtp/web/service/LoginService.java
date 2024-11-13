package org.imtp.web.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.web.config.AuthProperties;
import org.imtp.web.config.RedisSecurityContextRepository;
import org.imtp.web.config.RefreshAuthenticationToken;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.common.enums.ClientType;
import org.imtp.web.utils.EncryptUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private UserService userService;

    @Resource
    private TokenService tokenService;

    @Resource
    private RedisSecurityContextRepository securityContextRepository;

    @Resource
    private AuthProperties authProperties;

    public TokenInfo login(Authentication authenticationToken){

        return login(authenticationToken,false,ClientType.WEB);
    }

    public TokenInfo login(Authentication authenticationToken,boolean rememberMe,ClientType clientType){
        Authentication authenticate;
        if ((authenticate = SecurityContextHolder.getContext().getAuthentication()) == null || authenticate instanceof AnonymousAuthenticationToken) {
            authenticate = authenticationManager.authenticate(authenticationToken);
        }
        User user = (User)authenticate.getPrincipal();
        //如果是刷新token
        if (authenticate instanceof RefreshAuthenticationToken){
            authenticate = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        }
        //记住我token
        String rememberMeToken = null;
        if (rememberMe || authenticate instanceof RememberMeAuthenticationToken){
            rememberMeToken = rememberMeToken(user.getUsername(), user.getPassword());
        }
        //生成token
        TokenInfo tokenInfo = tokenService.generate(user, clientType);
        tokenInfo.setRememberMeToken(rememberMeToken);
        //序列化securityContext
        saveSecurityContext(user.getId(),authenticate);
        return tokenInfo;
    }

    private String rememberMeToken(String username,String password){
        Long configExpiration = authProperties.getRememberMe().getExpiration();
        long timestamp = configExpiration * 1000;
        long expiration = System.currentTimeMillis() + timestamp;
        String encryptStr = EncryptUtil.sha256(username, Long.toString(expiration), password, authProperties.getRememberMe().getSecretKey());
        return EncryptUtil.base64Encode(username, Long.toString(expiration), TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256.name(), encryptStr);
    }

    private void saveSecurityContext(Long userId,Authentication authenticate){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticate);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            throw new NullPointerException("Servlet is null");
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        request.setAttribute(RedisSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME,userId.toString());
        securityContextRepository.saveContext(securityContext,request,response);
    }

}
