package org.imtp.web.config;

import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.ClientType;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.utils.EncryptUtil;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

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
        if (tokenValid(token)){
            RefreshTokenPayloadInfo refreshTokenPayloadInfo = extractPayloadInfo(token);
            return RefreshAuthenticationToken.unauthenticated(refreshTokenPayloadInfo.getSubject(),null,refreshTokenPayloadInfo.getClientType());
        }
        return null;
    }

    public boolean tokenValid(String token){
        if (!tokenService.isValid(token, TokenType.REFRESH_TOKEN)){
            log.warn("该refreshToken已失效");
            return false;
        }
        log.info("签名校验成功");
        return true;
    }


    private static boolean isValidTokensLength(String[] tokens) {
        return tokens.length == 5;
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


    public static <T> T extractPayloadInfo(String token,Function<RefreshTokenServices.RefreshTokenPayloadInfo,T> claimsResolver){
        RefreshTokenServices.RefreshTokenPayloadInfo refreshTokenPayloadInfo = extractPayloadInfo(token);
        return extractPayloadInfo(refreshTokenPayloadInfo,claimsResolver);
    }

    public static <T> T extractPayloadInfo(RefreshTokenServices.RefreshTokenPayloadInfo refreshTokenPayloadInfo, Function<RefreshTokenServices.RefreshTokenPayloadInfo,T> claimsResolver){
        return claimsResolver.apply(refreshTokenPayloadInfo);
    }

    public static RefreshTokenServices.RefreshTokenPayloadInfo extractPayloadInfo(String token){
        String base64DecodeStr = EncryptUtil.base64Decode(token);
        String[] tokens = base64DecodeStr.split(":");
        if (!isValidTokensLength(tokens)){
            throw new RuntimeException("token length should be 5 but only " + tokens.length);
        }
        return new RefreshTokenServices.RefreshTokenPayloadInfo(tokens);
    }

    public static class RefreshTokenPayloadInfo {
        private String id;
        private String subject;
        private Long expiration;
        private ClientType clientType;
        private String algorithm;

        public RefreshTokenPayloadInfo(String[] tokens){
            this.id = tokens[4];
            this.subject = tokens[0];
            this.expiration = Long.parseLong(tokens[1]);
            this.clientType = ClientType.valueOf(tokens[2]);
            this.algorithm = tokens[3];

        }

        public String getId() {
            return id;
        }


        public String getSubject() {
            return subject;
        }

        public Long getExpiration() {
            return expiration;
        }

        public ClientType getClientType() {
            return clientType;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }

}
