package org.imtp.web.config.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 自定义令牌解析  解析请求头Authorization 以 token 开头的令牌
 * @Author ys
 * @Date 2023/10/11 15:18
 */
public class OAuth2BearerTokenResolver implements BearerTokenResolver {

    private static final Pattern authorizationPattern = Pattern.compile("^token (?<token>[a-zA-Z0-9-._~+/]+=*)$", 2);
    private String bearerTokenHeaderName = "Authorization";

    public OAuth2BearerTokenResolver() {
    }

    public String resolve(final HttpServletRequest request) {
        return this.resolveFromAuthorizationHeader(request);
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(this.bearerTokenHeaderName);
        if (!StringUtils.startsWithIgnoreCase(authorization, "token")) {
            return null;
        } else {
            Matcher matcher = authorizationPattern.matcher(authorization);
            if (!matcher.matches()) {
                BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
                throw new OAuth2AuthenticationException(error);
            } else {
                return matcher.group("token");
            }
        }
    }
}
