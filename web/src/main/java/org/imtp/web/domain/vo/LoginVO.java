package org.imtp.web.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.common.enums.ClientType;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/19 11:09
 */
@Getter
@Setter
public class LoginVO {

    public LoginVO(){}

    public LoginVO(TokenInfo token){
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.clientType = token.getClientType();
        this.expiration = token.getExpiration();
    }

    private String accessToken;

    private String refreshToken;

    private ClientType clientType;

    private Long expiration;

    private String rememberMeToken;

}
