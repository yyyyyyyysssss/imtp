package org.imtp.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/3 17:59
 */
@Getter
@Setter
public class MicrosoftTokenVO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    private String scope;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("id_token")
    private String idToken;

    @Override
    public String toString() {
        return "GoogleTokenVO{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                ", scope='" + scope + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}
