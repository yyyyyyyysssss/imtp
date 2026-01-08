package org.imtp.api.config.security;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @Description
 * @Author ys
 * @Date 2025/12/15 16:39
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final Cache<String, Boolean> cache;

    public CustomLogoutSuccessHandler(Cache<String, Boolean> permissionCache) {
        this.cache = permissionCache;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            // 清除该用户权限校验的缓存
            String name = authentication.getName();
            cache.asMap().keySet().stream()
                    .filter(key -> key.startsWith(name + ":"))
                    .forEach(cache::invalidate);
        }
    }
}
