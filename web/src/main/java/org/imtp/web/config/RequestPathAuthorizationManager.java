package org.imtp.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/10 13:38
 */
@Slf4j
public class RequestPathAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final AuthorizationDecision DENY = new AuthorizationDecision(false);

    private static final AuthorizationDecision AFFIRM = new AuthorizationDecision(true);


    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final static PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final static String URL_SEPARATOR = ",";

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext requestAuthorizationContext) {
        //当前请求路径
        String requestUrl = requestAuthorizationContext.getRequest().getRequestURI();
        log.info("当前请求路径:{}",requestUrl);
        Authentication authentication = supplier.get();
        //匿名用户
        boolean isAnonymous = authentication != null && !this.trustResolver.isAnonymous(authentication)
                && authentication.isAuthenticated();
        if(!isAnonymous) {
            return DENY;
        }
        //获取已登录用户的权限信息
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()){
            return DENY;
        }
        List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> (RequestUrlAuthority) m).toList();
        for (RequestUrlAuthority urlAuthority : requestUrlAuthorities){
            String urlStr = urlAuthority.getUrls();
            if (urlStr == null || urlStr.isEmpty()){
                continue;
            }
            String[] urls = urlStr.split(URL_SEPARATOR);
            List<String> urlList = Arrays.asList(urls);
            boolean b = urlList.parallelStream().anyMatch(m -> PATH_MATCHER.match(m, requestUrl));
            if (b){
                return AFFIRM;
            }
        }
        return DENY;
    }
}
