package org.imtp.web.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/9 22:37
 */
public class RefreshAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private String clientType;

    public RefreshAuthenticationToken(Object principal, Object credentials,String clientType) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.clientType = clientType;
        this.setAuthenticated(false);
    }

    public RefreshAuthenticationToken(Object principal, Object credentials,String clientType, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.clientType = clientType;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getClientType() {
        return clientType;
    }
}
