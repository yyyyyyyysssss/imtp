package org.imtp.web.config.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2023/10/11 10:37
 */
public class JwtGrantedScopeAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private String authorityPrefix = "SCOPE_";
    private String authoritiesClaimName;

    public JwtGrantedScopeAuthoritiesConverter() {
    }

    public JwtGrantedScopeAuthoritiesConverter(String authorityPrefix) {
        this.authorityPrefix = authorityPrefix;
    }

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String authClaimName = getAuthoritiesClaimName(jwt);
        if (StringUtils.isEmpty(authClaimName)){
            return Collections.emptyList();
        }
        List<String> scopes = jwt.getClaim(authClaimName);
        return scopes.stream()
                .map(authority -> new SimpleGrantedAuthority(this.authorityPrefix + authority))
                .collect(Collectors.toList());
    }

    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        } else {
            Iterator<String> var2 = WELL_KNOWN_AUTHORITIES_CLAIM_NAMES.iterator();

            String claimName;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                claimName = var2.next();
            } while (!jwt.hasClaim(claimName));

            return claimName;
        }
    }

}
