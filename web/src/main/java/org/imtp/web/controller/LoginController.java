package org.imtp.web.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.EmailAuthenticationProvider;
import org.imtp.web.config.EmailAuthenticationToken;
import org.imtp.web.config.RedisSecurityContextRepository;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.EmailInfo;
import org.imtp.web.domain.dto.LoginDTO;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.vo.LoginVO;
import org.imtp.web.service.EmailService;
import org.imtp.web.service.LoginService;
import org.imtp.web.service.TokenService;
import org.imtp.web.service.UserService;
import org.imtp.web.utils.VerificationCodeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/15 9:26
 */
@RestController
@Slf4j
public class LoginController {

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
    private LoginService loginService;

    @Resource
    private EmailService emailService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> login(@RequestBody @Validated LoginDTO loginDTO) {
        TokenInfo tokenInfo;
        switch (loginDTO.getLoginType()) {
            case NORMAL:
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getCredential());
                tokenInfo = loginService.login(authenticationToken, loginDTO.rememberMe(),loginDTO.getClientType());
                break;
            case EMAIL:
                EmailAuthenticationToken emailAuthenticationToken = new EmailAuthenticationToken(loginDTO.getUsername(), loginDTO.getCredential());
                tokenInfo = loginService.login(emailAuthenticationToken, loginDTO.rememberMe(),loginDTO.getClientType());
                break;
            default:
                throw new UnsupportedOperationException("不支持的登录方式:" + loginDTO.getLoginType());
        }
        if (tokenInfo == null) {
            throw new BadCredentialsException("Bad Credentials");
        }
        return ResultGenerator.ok(tokenInfo);
    }


    @GetMapping("/refreshToken")
    public Result<?> refreshToken() {
        String tokenStr = bearerTokenResolver.resolve(request);
        TokenInfo token = tokenService.refreshToken(tokenStr);
        Long userId = token.getUserId();
        UserDetails userDetails = userService.loadUserByUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        saveSecurityContext(userId, securityContext);
        return ResultGenerator.ok(new LoginVO(token));
    }

    @GetMapping("/sendEmailVerificationCode")
    public Result<?> sendEmailVerificationCode(@RequestParam("email") String email) {
        EmailInfo emailInfo = EmailInfo
                .builder()
                .title("邮箱验证码")
                .to(new String[]{email})
                .build();
        String verificationCode = VerificationCodeUtil.genVerificationCode();
        redisTemplate.opsForValue().set(EmailAuthenticationProvider.EMAIL_VERIFICATION_CODE_PREFIX + email, verificationCode);
        Map<String, Object> variable = new HashMap<>();
        variable.put("verificationCode", verificationCode);
        emailService.sendHtmlEmail(emailInfo, "EmailVerificationCode", variable);
        return ResultGenerator.ok();
    }

    private void saveSecurityContext(Long userId, SecurityContext securityContext) {
        request.setAttribute(RedisSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME, userId.toString());
        securityContextRepository.saveContext(securityContext, request, response);
    }


}
