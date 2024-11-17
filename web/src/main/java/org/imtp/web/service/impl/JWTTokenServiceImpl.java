package org.imtp.web.service.impl;

import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.ClientType;
import org.imtp.web.config.AuthProperties;
import org.imtp.web.config.RefreshTokenServices;
import org.imtp.web.config.idwork.IdGen;
import org.imtp.web.config.redis.RedisKey;
import org.imtp.web.config.redis.RedisWrapper;
import org.imtp.web.domain.dto.TokenDTO;
import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.utils.EncryptUtil;
import org.imtp.web.utils.JwtUtil;
import org.imtp.web.utils.PayloadInfo;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

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
        Long userId = user.getId();
        String accessToken = generateAccessToken(userId,clientType);
        Tuple2<String,String> tuple2 = generateRefreshToken(userId,clientType);
        String refreshToken = tuple2.getV1();
        String refreshTokenId = tuple2.getV2();
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
                TokenDTO tokenDTO = (TokenDTO) array[i];
                String atId = tokenDTO.getAccessTokenId();
                long expiration = tokenDTO.getExpiration() - System.currentTimeMillis();
                revokeToken(atId,expiration / 1000);
            }
            redisWrapper.removeZSet(key,array);
        }
        //token过期时间作为score 刷新token过期时间作为key的过期时间
        long expirationAt = payloadInfo.getExpiration();
        long expiration = expirationAt - System.currentTimeMillis();
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessTokenId(payloadInfo.getId());
        tokenDTO.setRefreshTokenId(refreshTokenId);
        tokenDTO.setExpiration(payloadInfo.getExpiration());
        redisWrapper.addZSet(key, tokenDTO, payloadInfo.getExpiration(), Duration.ofMillis(expiration));
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
    public boolean isValid(String token, TokenType tokenType) {
        switch (tokenType){
            case ACCESS_TOKEN :
                PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(token);
                return !redisWrapper.hasKey(RedisKey.TOKEN_BLACKLIST + payloadInfo.getId())
                        && JwtUtil.verifier(token)
                        && tokenType.equals(payloadInfo.getTokenType());
            case REFRESH_TOKEN:
                String base64DecodeStr = EncryptUtil.base64Decode(token);
                String[] tokens = base64DecodeStr.split(":");
                return !redisWrapper.hasKey(RedisKey.TOKEN_BLACKLIST + tokens[4]);
            default:
                throw new UnsupportedOperationException("不支持的token类型: " + tokenType);
        }
    }

    private String key(Long userId, ClientType clientType) {
        return RedisKey.USER_TOKEN + userId + ":" + clientType;
    }

    private String generateAccessToken(Long userId,ClientType clientType){

        return JwtUtil.genToken(userId.toString(),clientType);
    }

    private Tuple2<String,String> generateRefreshToken(Long userId, ClientType clientType){
        Long configExpiration = authProperties.getJwt().getRefreshExpiration();
        long timestamp = configExpiration * 1000;
        long expiration = System.currentTimeMillis() + timestamp;
        String encryptStr = EncryptUtil.sha256(userId.toString(), Long.toString(expiration),clientType.name(), authProperties.getJwt().getSecretKey());
        String token = EncryptUtil.base64Encode(userId.toString(), Long.toString(expiration), clientType.name(), RefreshTokenServices.RefreshTokenAlgorithm.SHA256.name(), encryptStr);
        return new Tuple2<>(token,encryptStr);
    }
}
