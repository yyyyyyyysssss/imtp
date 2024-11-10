package org.imtp.web.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.AuthProperties;
import org.imtp.web.config.RefreshTokenServices;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.config.redis.RedisKey;
import org.imtp.web.config.redis.RedisWrapper;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.common.enums.ClientType;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.utils.EncryptUtil;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/16 15:14
 */
@Slf4j
@Service
public class JWTTokenServiceImpl implements TokenService {

    @Resource
    private RedisWrapper redisWrapper;

    @Resource
    private AuthProperties authProperties;

    @Override
    public TokenInfo generate(User user, ClientType clientType) {
        return generate(user.getId(), clientType);
    }

    private TokenInfo generate(Long userId, ClientType clientType) {
        String accessToken = generateAccessToken(userId,clientType);
        String refreshToken = generateRefreshToken(userId,clientType);
        PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(accessToken);
        TokenInfo token = TokenInfo.builder()
                .id(IdGen.genId())
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiration(payloadInfo.getExpiration())
                .clientType(clientType)
                .build();
        String key = key(userId, clientType);

        Set<Object> tokens = redisWrapper.rangeAllZSet(key);
        int reserveQty = authProperties.getCoexistToken() - 1;
        if (tokens != null && !tokens.isEmpty() && tokens.size() > reserveQty) {
            int delQty = tokens.size() - reserveQty;
            Object[] array = Arrays.copyOfRange(tokens.toArray(),0, delQty);
            for (int i = 0; i < delQty; i++) {
                revokeToken((String) array[i],authProperties.getJwt().getExpiration());
            }
            redisWrapper.removeZSet(key,array);
        }
        //token过期时间作为score 刷新token过期时间作为key的过期时间
        long expirationAt = payloadInfo.getExpiration();
        long expiration = expirationAt - System.currentTimeMillis();
        redisWrapper.addZSet(key, payloadInfo.getId(), payloadInfo.getExpiration(), Duration.ofMillis(expiration));
        return token;
    }

    @Override
    public void revokeToken(String token) {
        PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(token);
        long expiration = payloadInfo.getExpiration() - System.currentTimeMillis();
        revokeToken(payloadInfo.getId(),expiration / 1000);
    }

    private void revokeToken(String tokenId,Long expiration) {
        //加入黑名单
        redisWrapper.setValue(RedisKey.TOKEN_BLACKLIST + tokenId,null,Duration.ofSeconds(expiration));
    }

    @Override
    public void revokeUserAllTokens(String userId) {
        String k = RedisKey.USER_TOKEN + userId;
        redisWrapper.delete(k);
    }

    @Override
    public boolean isValid(String token) {
        return isValid(token,TokenType.ACCESS_TOKEN);
    }

    @Override
    public boolean isValid(String token, TokenType tokenType) {
        PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(token);
        return !redisWrapper.hasKey(RedisKey.TOKEN_BLACKLIST + payloadInfo.getId()) && JwtUtil.verifier(token) && tokenType.equals(payloadInfo.getTokenType());
    }

    private String key(Long userId, ClientType clientType) {
        return RedisKey.USER_TOKEN + userId + ":" + clientType;
    }

    private String generateAccessToken(Long userId,ClientType clientType){

        return JwtUtil.genToken(userId.toString(),clientType);
    }

    private String generateRefreshToken(Long userId,ClientType clientType){
        Long configExpiration = authProperties.getJwt().getRefreshExpiration();
        long timestamp = configExpiration * 1000;
        long expiration = System.currentTimeMillis() + timestamp;
        String encryptStr = EncryptUtil.sha256(userId.toString(), Long.toString(expiration),clientType.name(), authProperties.getJwt().getSecretKey());
        return EncryptUtil.base64Encode(userId.toString(), Long.toString(expiration),clientType.name(), RefreshTokenServices.RefreshTokenAlgorithm.SHA256.name(), encryptStr);
    }
}
