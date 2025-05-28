package org.imtp.api.config.security.authorization;

import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 基于请求路径的权限管理器
 * @Author ys
 * @Date 2024/7/10 13:38
 */
@Slf4j
public class RequestPathAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final AuthorizationDecision DENY = new AuthorizationDecision(false);

    private static final AuthorizationDecision AFFIRM = new AuthorizationDecision(true);

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private final static String URL_SEPARATOR = ",";

    private static final Pattern METHOD_PREFIX_PATTERN = Pattern.compile("^(GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD|\\*):(.+)$", Pattern.CASE_INSENSITIVE);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext requestAuthorizationContext) {
        //当前请求路径
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
        RequestMatcher requestMatcher;
        List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> (RequestUrlAuthority) m).filter(f -> f.getUrls() != null && !f.getUrls().isBlank()).toList();
        for (RequestUrlAuthority urlAuthority : requestUrlAuthorities){
            String urlStr = urlAuthority.getUrls();
            if (urlStr == null || urlStr.isEmpty()){
                continue;
            }
            boolean b = false;
            String[] urls = urlStr.split(URL_SEPARATOR);
            for (String url : urls){
                Matcher httpMethodMatcher = METHOD_PREFIX_PATTERN.matcher(url);
                if (httpMethodMatcher.matches()){
                    String method = httpMethodMatcher.group(1);
                    String pattern = httpMethodMatcher.group(2);
                    requestMatcher = new PathPatternRequestMatcher(pattern,method);
                }else {
                    requestMatcher = new PathPatternRequestMatcher(url);
                }
                RequestMatcher.MatchResult matcher = requestMatcher.matcher(requestAuthorizationContext.getRequest());
                if (matcher.isMatch()){
                    b = true;
                    break;
                }
            }
            if (b){
                return AFFIRM;
            }
        }
        return DENY;
    }
}
