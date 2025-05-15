package org.imtp.api.config.security.authentication.refreshtoken;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.enums.TokenType;
import org.imtp.api.config.security.TokenService;
import org.imtp.api.utils.PayloadInfo;
import org.springframework.security.core.Authentication;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/9 17:58
 */
@Slf4j
public class RefreshTokenServices {

    private TokenService tokenService;

    public RefreshTokenServices(TokenService tokenService){
        this.tokenService = tokenService;
    }

    public Authentication refreshToken(String token){
        Tuple2<Boolean, PayloadInfo> valid = tokenService.isValid(token, TokenType.REFRESH_TOKEN);
        if (valid.getV1()){
            PayloadInfo payloadInfo = valid.getV2();
            return RefreshAuthenticationToken.unauthenticated(payloadInfo.getSubject(),null,payloadInfo.getClientType());
        }else {
            log.warn("该refreshToken已失效:{}",token);
        }
        return null;
    }

    public enum RefreshTokenAlgorithm {
        SHA256("SHA-256");

        private final String digestAlgorithm;

        RefreshTokenAlgorithm(String digestAlgorithm) {
            this.digestAlgorithm = digestAlgorithm;
        }

        public String getDigestAlgorithm() {
            return this.digestAlgorithm;
        }
    }

}
