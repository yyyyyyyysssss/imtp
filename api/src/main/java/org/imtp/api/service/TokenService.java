package org.imtp.api.service;

import groovy.lang.Tuple2;
import org.imtp.api.domain.entity.TokenInfo;
import org.imtp.api.utils.PayloadInfo;
import org.imtp.common.enums.ClientType;
import org.imtp.api.enums.TokenType;

public interface TokenService {

    TokenInfo generate(Long userId, ClientType clientType);

    void revokeToken(String token);

    Tuple2<Boolean, PayloadInfo> isValid(String token, TokenType tokenType);

}
