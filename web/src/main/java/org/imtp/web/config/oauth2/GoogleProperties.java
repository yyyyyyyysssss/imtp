package org.imtp.web.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.ClientType;
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

    private String redirectUrlWeb;

    private String redirectUrlApp;

    private String redirectUrl;

    private String userInfoUrl;

    public String getAuthCodeUrl() {
        return this.authCodeUrl + "&redirect_uri=" + redirectUrlWeb;
    }

    public String getAuthCodeUrl(ClientType clientType) {
        return clientType != null && clientType.equals(ClientType.APP) ?
                this.authCodeUrl + "&redirect_uri=" + redirectUrlApp
                :
                this.authCodeUrl + "&redirect_uri=" + redirectUrlWeb;
    }

    public String getRedirectUrl() {
        return redirectUrlWeb;
    }

    public String getRedirectUrl(ClientType clientType) {
        return clientType != null && clientType.equals(ClientType.APP) ?
                redirectUrlApp
                :
                redirectUrlWeb;
    }
}
