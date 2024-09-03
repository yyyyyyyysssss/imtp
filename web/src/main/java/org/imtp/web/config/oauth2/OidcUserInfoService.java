package org.imtp.web.config.oauth2;

import jakarta.annotation.Resource;
import org.imtp.web.domain.entity.User;
import org.imtp.web.service.UserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Description 自定义id_token中包含的信息
 * @Author ys
 * @Date 2023/9/28 21:25
 */
@Service
public class OidcUserInfoService {

    @Resource
    private UserService userService;

    public OidcUserInfo loadUser(String username) {
        User user = userService.findByUsername(username);
        if (user == null){
            return new OidcUserInfo(new HashMap<>());
        }
        return OidcUserInfo.builder()
                .subject(user.getId().toString())
                .name(user.getNickname())
                .gender(user.getGender().name())
                .preferredUsername(username)
                .picture(user.getAvatar())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .build();
    }

}
