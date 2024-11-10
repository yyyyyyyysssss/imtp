package org.imtp.web.service;

import org.imtp.web.domain.entity.TokenInfo;
import org.imtp.web.domain.entity.User;
import org.imtp.common.enums.ClientType;
import org.imtp.web.enums.TokenType;

public interface TokenService {

    TokenInfo generate(User user, ClientType clientType);

    void revokeToken(String token);

    void revokeUserAllTokens(String userId);

    boolean isValid(String token);

    boolean isValid(String token, TokenType tokenType);

}
