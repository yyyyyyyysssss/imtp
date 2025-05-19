package org.imtp.api.controller;

import org.imtp.api.domain.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 10:15
 */
public class BaseController {

    protected User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未登录或身份未验证");
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new AccessDeniedException("当前用户信息获取失败");
    }

    protected <T> T getCurrentUser(Function<User, T> function) {
        User user = getCurrentUser();
        return function.apply(user);
    }

}
