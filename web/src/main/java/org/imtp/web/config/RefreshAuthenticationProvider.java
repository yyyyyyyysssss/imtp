package org.imtp.web.config;

import org.imtp.web.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/5 9:52
 */
public class RefreshAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public RefreshAuthenticationProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        RefreshAuthenticationToken refreshAuthenticationToken = (RefreshAuthenticationToken) authentication;
        Object principal = refreshAuthenticationToken.getPrincipal();
        UserDetails userDetails = ((UserService) userDetailsService).loadUserByUserId(Long.parseLong(principal.toString()));
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        return RefreshAuthenticationToken.authenticated(userDetails,null,refreshAuthenticationToken.getClientType(),userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
