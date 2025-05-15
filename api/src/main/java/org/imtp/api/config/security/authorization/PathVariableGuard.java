package org.imtp.api.config.security.authorization;

import org.imtp.api.domain.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @Description 基于路径参数的权限控制
 * @Author ys
 * @Date 2025/4/10 14:03
 */
@Component("pathVariableGuard")
public class PathVariableGuard {

    public boolean checkUserId(Authentication authentication, String userId) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || userId == null || userId.isEmpty()) {
            return false;
        }
        if(authentication.getPrincipal() instanceof User user){
            return user.getId().toString().equals(userId);
        }
        return false;
    }

}
