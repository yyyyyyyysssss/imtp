package org.imtp.api.config.security;

import groovy.lang.Tuple2;
import org.imtp.api.domain.entity.TokenInfo;
import org.imtp.api.domain.entity.User;
import org.imtp.api.utils.PayloadInfo;
import org.imtp.common.enums.ClientType;
import org.imtp.api.enums.TokenType;

public interface TokenService {

    TokenInfo generate(User user, ClientType clientType, boolean rememberMe);

    void revokeToken(String token);

    Tuple2<Boolean, PayloadInfo> isValid(String token, TokenType tokenType);

}
