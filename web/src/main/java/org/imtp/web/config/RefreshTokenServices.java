package org.imtp.web.config;

import lombok.extern.slf4j.Slf4j;
import org.imtp.web.service.UserService;
import org.imtp.web.utils.EncryptUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Utf8;

import java.security.MessageDigest;
import java.util.Collections;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/9 17:58
 */
@Slf4j
public class RefreshTokenServices {

    private UserDetailsService userService;

    private String secretKey;

    private RedisTemplate<String,Object> redisTemplate;

    private final GrantedAuthority grantedAuthority = () -> "refresh_token";

    public RefreshTokenServices(String secretKey,UserDetailsService userService,RedisTemplate<String,Object> redisTemplate){
        this.secretKey = secretKey;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    public Authentication tokenValid(String token){
        String base64DecodeStr = EncryptUtil.base64Decode(token);
        String[] tokens = base64DecodeStr.split(":");
        if (!isValidTokensLength(tokens)){
            log.error("签名长度错误");
            return null;
        }
        long tokenExpiryTime = Long.parseLong(tokens[1]);
        if (isTokenExpired(tokenExpiryTime)){
            log.error("签名已过期");
            return null;
        }
        String clientType = tokens[2];
        String userId = tokens[0];
        UserDetails userDetails = ((UserService)this.userService).loadUserByUserId(Long.parseLong(userId));
        if (userDetails == null){
            log.error("根据签名中的用户信息查询为空");
            return null;
        }
        String alg = tokens[3];
        RefreshTokenAlgorithm actualAlgorithm = RefreshTokenAlgorithm.valueOf(alg);
        String actualTokenSignature = tokens[4];
        if (!actualAlgorithm.equals(RefreshTokenAlgorithm.SHA256)){
            log.error("不支持的算法");
            return null;
        }
        String expectedTokenSignature = EncryptUtil.sha256(userId, tokenExpiryTime + "",clientType, secretKey);
        if(!equals(expectedTokenSignature, actualTokenSignature)){
            log.error("当前签名: {} 预期签名: {}",actualTokenSignature,expectedTokenSignature);
            return null;
        }
        log.info("签名校验成功");
        return new RefreshAuthenticationToken(userId,actualTokenSignature,clientType, Collections.singleton(grantedAuthority));
    }


    private boolean isValidTokensLength(String[] tokens) {
        return tokens.length == 5;
    }

    protected boolean isTokenExpired(long tokenExpiryTime) {
        return tokenExpiryTime < System.currentTimeMillis();
    }

    private static boolean equals(String expected, String actual) {
        byte[] expectedBytes = bytesUtf8(expected);
        byte[] actualBytes = bytesUtf8(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static byte[] bytesUtf8(String s) {
        return s != null ? Utf8.encode(s) : null;
    }

    public static enum RefreshTokenAlgorithm {
        SHA256("SHA-256");

        private final String digestAlgorithm;

        private RefreshTokenAlgorithm(String digestAlgorithm) {
            this.digestAlgorithm = digestAlgorithm;
        }

        public String getDigestAlgorithm() {
            return this.digestAlgorithm;
        }
    }

}
