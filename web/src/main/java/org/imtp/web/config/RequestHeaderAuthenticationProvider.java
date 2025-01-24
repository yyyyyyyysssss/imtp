package org.imtp.web.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/10 10:00
 */
public class RequestHeaderAuthenticationProvider implements AuthenticationProvider {

    private List<AuthProperties.RequestHeadAuthenticationConfig> requestHeadAuthentications;

    private Map<String, String> apikeyMap;

    private final static PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final static Set<? extends GrantedAuthority> REQUEST_HEADER_AUTHORITY = Collections.singleton((GrantedAuthority) () -> "request_header");

    private final static String URL_SEPARATOR = ",";

    public RequestHeaderAuthenticationProvider(List<AuthProperties.RequestHeadAuthenticationConfig> requestHeadAuthentications){
        if (requestHeadAuthentications == null || requestHeadAuthentications.isEmpty()){
            throw new NullPointerException("requestHeadAuthentications not null");
        }
        this.apikeyMap = requestHeadAuthentications.stream().collect(Collectors.toMap(AuthProperties.RequestHeadAuthenticationConfig::getApikey, AuthProperties.RequestHeadAuthenticationConfig::getAntPath));
        this.requestHeadAuthentications = requestHeadAuthentications;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object principal = authentication.getPrincipal();
        if (principal == null){
            throw new BadCredentialsException("Bad Request Header Principal");
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            throw new BadCredentialsException("Bad Request Header Not http request");
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String requestUrl = request.getRequestURI();
        boolean valid = false;
        String antPath = apikeyMap.get((String) principal);
        if (antPath != null && !antPath.isEmpty()){
            String[] urls = antPath.split(URL_SEPARATOR);
            List<String> urlList = Arrays.asList(urls);
            if(urlList.stream().anyMatch(m -> PATH_MATCHER.match(m, requestUrl))){
                valid = true;
            }
        }
        if (!valid){
            throw new BadCredentialsException("Bad Request Header Credentials");
        }
        return new PreAuthenticatedAuthenticationToken(principal,null,REQUEST_HEADER_AUTHORITY);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
