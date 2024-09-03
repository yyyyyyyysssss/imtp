package org.imtp.web.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description  用于测试本系统的oauth登录流程
 * @Author ys
 * @Date 2023/9/20 20:12
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "auth.other-login.self")
@Component
public class SelfProperties {

    private String clientName;

    private String clientId;

    private String clientSecret;

    private String redirectUrl;

    private String scope;

    private String authCodeUrl;

    private String deviceCodeUrl;

    private String tokenUrl;

    private String userInfoUrl;

}
