package org.imtp.api.utils;

import org.imtp.api.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 15:36
 */
public class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    public static <T> T getCurrentUser(Function<User, T> function) {
        User user = getCurrentUser();
        return user != null ? function.apply(user) : null;
    }

}
