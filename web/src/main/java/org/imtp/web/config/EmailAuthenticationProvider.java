package org.imtp.web.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/13 11:38
 */
public class EmailAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private RedisTemplate<String, Object> redisTemplate;

    public static final String EMAIL_VERIFICATION_CODE_PREFIX = "email:verification:code:";

    public EmailAuthenticationProvider(UserDetailsService userDetailsService,RedisTemplate<String, Object> redisTemplate){
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        EmailAuthenticationToken emailAuthenticationToken = (EmailAuthenticationToken) authentication;
        Object principal = emailAuthenticationToken.getPrincipal();
        Object credentials = emailAuthenticationToken.getCredentials();
        UserDetails userDetails = userDetailsService.loadUserByUsername((String) principal);
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        Object verificationCode = redisTemplate.opsForValue().get(EMAIL_VERIFICATION_CODE_PREFIX + principal);
        if (verificationCode == null || !verificationCode.equals(credentials)){
            throw new BadCredentialsException("验证码错误!");
        }
        redisTemplate.delete(EMAIL_VERIFICATION_CODE_PREFIX + principal);
        EmailAuthenticationToken authenticated = EmailAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticated.setDetails(emailAuthenticationToken.getDetails());
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
