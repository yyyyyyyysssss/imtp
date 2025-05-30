package org.imtp.api.config.security.authorization;

import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.imtp.api.domain.entity.AuthorityUrl;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
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
        List<RequestUrlAuthority> requestUrlAuthorities = authorities.stream().map(m -> (RequestUrlAuthority) m).filter(f -> f.getUrls() != null && !CollectionUtils.isEmpty(f.getUrls())).toList();
        for (RequestUrlAuthority urlAuthority : requestUrlAuthorities){
            List<AuthorityUrl> urls = urlAuthority.getUrls();
            if (urls == null || urls.isEmpty()){
                continue;
            }
            boolean b = false;
            for (AuthorityUrl authorityUrl : urls){
                if(authorityUrl.getMethod() != null && !authorityUrl.getMethod().isBlank()){
                    //如果有指定请求方法，则使用指定的请求方法
                    requestMatcher = new PathPatternRequestMatcher(authorityUrl.getUrl(),authorityUrl.getMethod());
                }else {
                    requestMatcher = new PathPatternRequestMatcher(authorityUrl.getUrl());
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
