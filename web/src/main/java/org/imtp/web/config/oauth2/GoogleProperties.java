package org.imtp.web.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2023/9/20 20:12
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "auth.other-login.google")
@Component
public class GoogleProperties {

    private String clientName;

    private String clientId;

    private String clientSecret;

    private String scope;

    private String authCodeUrl;

    private String tokenUrl;

    private String redirectUrl;

    private String userInfoUrl;

}
