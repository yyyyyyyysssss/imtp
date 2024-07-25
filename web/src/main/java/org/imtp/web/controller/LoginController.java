package org.imtp.web.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imtp.web.config.AuthProperties;
import org.imtp.web.config.BusinessException;
import org.imtp.web.config.RedisSecurityContextRepository;
import org.imtp.web.config.response.Result;
import org.imtp.web.config.response.ResultGenerator;
import org.imtp.web.domain.dto.LoginDTO;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.web.domain.vo.LoginVO;
import org.imtp.web.enums.ClientType;
import org.imtp.web.service.TokenService;
import org.imtp.web.service.UserService;
import org.imtp.web.utils.EncryptUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/15 9:26
 */
@RestController
@Slf4j
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    @Resource
    private UserService userService;

    @Resource
    private BearerTokenResolver bearerTokenResolver;

    @Resource
    private RedisSecurityContextRepository securityContextRepository;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Resource
    private AuthProperties authProperties;

    @PostMapping("/login")
    public Result<?> login(@RequestBody @Validated LoginDTO loginDTO){
        Authentication authentication;
        if ((authentication = SecurityContextHolder.getContext().getAuthentication()) == null || authentication instanceof AnonymousAuthenticationToken) {
            if (StringUtils.isEmpty(loginDTO.getUsername()) || StringUtils.isEmpty(loginDTO.getPassword())){
                throw new BusinessException("用户名或密码不能为空");
            }
            switch (loginDTO.getLoginType()){
                case NORMAL :
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
                    authentication = authenticationManager.authenticate(authenticationToken);
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的登录方式:" + loginDTO.getLoginType());
            }
        }
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        //获取本地用户信息
        User user = userService.findByUsername(principal.getUsername());
        TokenInfo token = tokenService.generate(user, ClientType.PC);
        LoginVO loginVO = new LoginVO(token);
        //勾选记住密码或使用记住密码登录时重新生成RememberMeToken
        if (loginDTO.rememberMe() || authentication instanceof RememberMeAuthenticationToken){
            String rememberMeToken = rememberMeToken(user.getUsername(), user.getPassword());
            loginVO.setRememberMeToken(rememberMeToken);
        }
        //序列化securityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        saveSecurityContext(token.getUserId(),securityContext);

        return ResultGenerator.ok(loginVO);
    }


    @GetMapping("/refreshToken")
    public Result<?> refreshToken(){
        String tokenStr = bearerTokenResolver.resolve(request);
        TokenInfo token = tokenService.refreshToken(tokenStr);
        Long userId = token.getUserId();
        UserDetails userDetails = userService.loadUserByUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        saveSecurityContext(userId,securityContext);
        return ResultGenerator.ok(new LoginVO(token));
    }

    private void saveSecurityContext(Long userId,SecurityContext securityContext){
        request.setAttribute(RedisSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME,userId.toString());
        securityContextRepository.saveContext(securityContext,request,response);
    }

    private String rememberMeToken(String username,String password){
        Long configExpiration = authProperties.getRememberMe().getExpiration();
        long timestamp = configExpiration * 1000;
        long expiration = System.currentTimeMillis() + timestamp;
        String encryptStr = EncryptUtil.sha256(username, Long.toString(expiration), password, authProperties.getRememberMe().getSecretKey());
        return EncryptUtil.base64Encode(username, Long.toString(expiration), TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256.name(), encryptStr);
    }


}
