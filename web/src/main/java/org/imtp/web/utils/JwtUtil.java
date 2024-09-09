package org.imtp.web.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.utils.JsonUtil;
import org.imtp.web.config.AuthProperties;
import org.imtp.common.enums.ClientType;
import org.imtp.web.enums.TokenType;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/14 12:31
 */
@Slf4j
@Component
public class JwtUtil {

    @Resource
    private AuthProperties authProperties;

    private static AuthProperties staticAuthProperties;

    @PostConstruct
    public void init(){
        staticAuthProperties = authProperties;
    }

    public static String genToken(String subject,ClientType clientType){

        return buildToken(subject,clientType,TokenType.ACCESS_TOKEN,staticAuthProperties.getJwt().getExpiration());
    }

    public static String genRefreshToken(String subject,ClientType clientType){

        return buildToken(subject,clientType,TokenType.REFRESH_TOKEN,staticAuthProperties.getJwt().getRefreshExpiration());
    }

    //从token中获取用户
    public static String extractUserId(String token) {
        return extractPayloadInfo(token, PayloadInfo::getSubject);
    }

    public static <T> T extractPayloadInfo(String token, Function<PayloadInfo, T> claimsResolver) {
        final PayloadInfo payloadInfo = extractPayloadInfo(token);
        return claimsResolver.apply(payloadInfo);
    }

    public static boolean verifier(String token){
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            MACVerifier macVerifier = new MACVerifier(staticAuthProperties.getJwt().getSecretKey());
            if (!jwsObject.verify(macVerifier)){
                throw new JOSEException("签名不合法");
            }
            PayloadInfo payloadInfo = extractPayloadInfo(token);
            if (payloadInfo.getExpiration() < System.currentTimeMillis()){
                return false;
            }
            return true;
        }catch (JOSEException | ParseException e){
            log.error("jwt token verification failed");
            return false;
        }
    }

    public static PayloadInfo extractPayloadInfo(String token){
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            String payloadInfoJson = jwsObject.getPayload().toString();
            return JsonUtil.parseObject(payloadInfoJson, PayloadInfo.class);
        }catch (ParseException e){
            log.error("jwt token parse failed");
            throw new RuntimeException("jwt token parse failed");
        }
    }

    private static String buildToken(String subject, ClientType clientType, TokenType tokenType,long expiration){
        JWSHeader jwsHeader = new JWSHeader
                .Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimestamp = currentTimeMillis + (expiration * 1000);
        PayloadInfo payloadInfo = PayloadInfo.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .clientType(clientType)
                .tokenType(tokenType)
                .issuedAt(currentTimeMillis)
                .expiration(expirationTimestamp)
                .build();
        Payload payload = new Payload(JsonUtil.toJSONString(payloadInfo));
        MACSigner signer;
        try {
            signer = new MACSigner(staticAuthProperties.getJwt().getSecretKey());
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("create jwt token error : ",e);
            throw new RuntimeException("create jwt token error");
        }
    }

}
