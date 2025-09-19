package org.imtp.api.config.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Description
 * @Author ys
 * @Date 2025/8/29 16:09
 */
public class UsernamePasswordAuthenticationProvider extends DaoAuthenticationProvider {

    private final LoginAttemptService loginAttemptService;

    public UsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService,LoginAttemptService loginAttemptService) {
        super(userDetailsService);
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        // 1. 检查短期锁定（Redis）
        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("账号已锁定");
        }
        try {
            Authentication authenticate = super.authenticate(authentication);
            // 3. 登录成功 → 清理失败次数
            loginAttemptService.loginSucceeded(username);
            return authenticate;
        }catch (BadCredentialsException e) {
            // 4. 登录失败 → 记录失败次数
            loginAttemptService.loginFailed(username);
            throw e;
        }
    }
}
