package org.imtp.api.service.impl;

import groovy.lang.Tuple2;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.enums.ClientType;
import org.imtp.api.config.AuthProperties;
import org.imtp.api.config.RefreshTokenServices;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.redis.RedisKey;
import org.imtp.api.config.redis.RedisWrapper;
import org.imtp.api.domain.dto.TokenDTO;
import org.imtp.api.domain.entity.TokenInfo;
import org.imtp.api.domain.entity.User;
import org.imtp.api.enums.TokenType;
import org.imtp.api.service.TokenService;
import org.imtp.api.utils.EncryptUtil;
import org.imtp.api.utils.JwtUtil;
import org.imtp.api.utils.PayloadInfo;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
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
    public TokenInfo generate(Long userId, ClientType clientType) {
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
                TokenDTO tokenDTO = (TokenDTO) array[i];
                revokeToken(tokenDTO);
            }
            redisWrapper.removeZSet(key,array);
        }
        //token过期时间作为score 刷新token过期时间作为key的过期时间
        long expirationAt = payloadInfo.getExpiration();
        long expiration = expirationAt - System.currentTimeMillis();
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessTokenId(payloadInfo.getId());
        tokenDTO.setRefreshToken(refreshToken);
        tokenDTO.setExpiration(payloadInfo.getExpiration());
        redisWrapper.addZSet(key, tokenDTO, payloadInfo.getExpiration(), Duration.ofMillis(expiration));
        return token;
    }

    @Override
    public void revokeToken(String token) {
        PayloadInfo payloadInfo = JwtUtil.extractPayloadInfo(token);
        String userId = payloadInfo.getSubject();
        String key = key(Long.parseLong(userId), payloadInfo.getClientType());
        Set<Object> tokens = redisWrapper.rangeAllZSet(key);
        for (Object object : tokens){
            TokenDTO tokenDTO = (TokenDTO) object;
            if (tokenDTO.getAccessTokenId().equals(payloadInfo.getId())){
                revokeToken(tokenDTO);
                redisWrapper.removeZSet(key,tokenDTO);
            }
        }
    }

    private void revokeToken(TokenDTO tokenDTO){
        long currentTimeMillis = System.currentTimeMillis();
        //accessToken 加入黑名单
        String atId = tokenDTO.getAccessTokenId();
        long accessTokenExpiration = tokenDTO.getExpiration() - currentTimeMillis;
        revokeToken(atId,accessTokenExpiration);
        //refreshToken 加入黑名单
        String rt = tokenDTO.getRefreshToken();
        String[] rtStr = EncryptUtil.base64Decode(rt).split(":");
        long refreshTokenExpiration = Long.parseLong(rtStr[1]) - currentTimeMillis;
        revokeToken(rtStr[4],refreshTokenExpiration);
    }

    private void revokeToken(String tokenId,Long expiration) {
        //加入黑名单
        redisWrapper.setValue(RedisKey.TOKEN_BLACKLIST + tokenId,null,Duration.ofMillis(expiration));
    }

    @Override
    public Tuple2<Boolean, PayloadInfo> isValid(String token, TokenType tokenType) {
        String tokenId;
        PayloadInfo payloadInfo;
        switch (tokenType){
            case ACCESS_TOKEN :
                if(!JwtUtil.verifier(token)){
                    log.warn("token已过期");
                    return new Tuple2<>(false, null);
                }
                payloadInfo = JwtUtil.extractPayloadInfo(token);
                if(!tokenType.equals(payloadInfo.getTokenType())){
                    log.warn("token类型不相符");
                    return new Tuple2<>(false, null);
                }
                tokenId = payloadInfo.getId();
                break;
            case REFRESH_TOKEN:
                RefreshTokenServices.RefreshTokenPayloadInfo refreshTokenPayloadInfo = RefreshTokenServices.extractPayloadInfo(token);
                long tokenExpiryTime = refreshTokenPayloadInfo.getExpiration();
                if (tokenExpiryTime < System.currentTimeMillis()){
                    log.warn("签名已过期");
                    return new Tuple2<>(false, null);
                }
                String clientType = refreshTokenPayloadInfo.getClientType().name();
                String userId = refreshTokenPayloadInfo.getSubject();
                String alg = refreshTokenPayloadInfo.getAlgorithm();
                RefreshTokenServices.RefreshTokenAlgorithm actualAlgorithm = RefreshTokenServices.RefreshTokenAlgorithm.valueOf(alg);
                String actualTokenSignature = refreshTokenPayloadInfo.getId();
                if (!actualAlgorithm.equals(RefreshTokenServices.RefreshTokenAlgorithm.SHA256)){
                    log.warn("不支持的算法");
                    return new Tuple2<>(false, null);
                }
                String secretKey = authProperties.getJwt().getSecretKey();
                String expectedTokenSignature = EncryptUtil.sha256(userId, tokenExpiryTime + "",clientType, secretKey);
                if(!equals(expectedTokenSignature, actualTokenSignature)){
                    log.warn("当前签名: {} 预期签名: {}",actualTokenSignature,expectedTokenSignature);
                    return new Tuple2<>(false, null);
                }
                tokenId = refreshTokenPayloadInfo.getId();
                payloadInfo = PayloadInfo
                        .builder()
                        .id(tokenId)
                        .subject(userId)
                        .clientType(refreshTokenPayloadInfo.getClientType())
                        .tokenType(TokenType.REFRESH_TOKEN)
                        .expiration(refreshTokenPayloadInfo.getExpiration())
                        .build();
                break;
            default:
                throw new UnsupportedOperationException("不支持的token类型: " + tokenType);
        }
        //黑名单
        if(redisWrapper.hasKey(RedisKey.TOKEN_BLACKLIST + tokenId)){
            log.warn("token已被加入黑名单");
            return new Tuple2<>(false, null);
        }
        return new Tuple2<>(true, payloadInfo);
    }

    private String key(Long userId, ClientType clientType) {
        return RedisKey.USER_TOKEN + userId + ":" + clientType;
    }

    private String generateAccessToken(Long userId,ClientType clientType){

        return JwtUtil.genToken(userId.toString(),clientType);
    }

    private String generateRefreshToken(Long userId, ClientType clientType){
        Long configExpiration = authProperties.getJwt().getRefreshExpiration();
        long timestamp = configExpiration * 1000;
        long expiration = System.currentTimeMillis() + timestamp;
        String encryptStr = EncryptUtil.sha256(userId.toString(), Long.toString(expiration),clientType.name(), authProperties.getJwt().getSecretKey());
        return EncryptUtil.base64Encode(userId.toString(), Long.toString(expiration), clientType.name(), RefreshTokenServices.RefreshTokenAlgorithm.SHA256.name(), encryptStr);
    }

    private static boolean equals(String expected, String actual) {
        byte[] expectedBytes = bytesUtf8(expected);
        byte[] actualBytes = bytesUtf8(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static byte[] bytesUtf8(String s) {
        return s != null ? Utf8.encode(s) : null;
    }
}
