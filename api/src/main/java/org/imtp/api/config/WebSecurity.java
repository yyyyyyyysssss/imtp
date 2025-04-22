package org.imtp.api.config;

import org.imtp.api.domain.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/10 14:03
 */
@Component("webSecurity")
public class WebSecurity {

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
