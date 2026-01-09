package org.imtp.api.config.security.authorization;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.imtp.api.domain.entity.AuthorityUrl;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

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

    private final Cache<String, Boolean> cache;

    private final PathPatternRequestMatcher.Builder builder;

    public RequestPathAuthorizationManager(Cache<String, Boolean> permissionCache){
        this.cache = permissionCache;
        builder = PathPatternRequestMatcher.withDefaults();
    }

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
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        String cacheKey = generateCacheKey(authentication, request.getRequestURI(), request.getMethod());
        Boolean cachedDecision  = cache.getIfPresent(cacheKey);
        // 缓存命中直接返回
        if (cachedDecision != null) {
            return cachedDecision ? AFFIRM : DENY;
        }
        //获取已登录用户的权限信息
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()){
            // 没有权限，缓存拒绝结果
            cache.put(cacheKey,false);
            return DENY;
        }
        List<RequestUrlAuthority> requestUrlAuthorities = authorities
                .stream()
                .filter(f -> f instanceof RequestUrlAuthority)
                .map(m -> (RequestUrlAuthority) m)
                .filter(f -> f.getUrls() != null && !CollectionUtils.isEmpty(f.getUrls()))
                .toList();
        for (RequestUrlAuthority urlAuthority : requestUrlAuthorities){
            List<AuthorityUrl> urls = urlAuthority.getUrls();
            if (matches(request,urls)){
                cache.put(cacheKey,true); // 缓存通过结果
                return AFFIRM;
            }
        }
        cache.put(cacheKey,false); // 缓存拒绝结果
        return DENY;
    }

    public boolean matches(HttpServletRequest request, List<AuthorityUrl> urls) {
        if(CollectionUtils.isEmpty(urls)){
            return false;
        }
        for (AuthorityUrl authorityUrl : urls){
            boolean matches;
            if(authorityUrl.getMethod() != null && !authorityUrl.getMethod().isBlank() && !authorityUrl.getMethod().equals("*")){
                //如果有指定请求方法，则使用指定的请求方法
                matches = builder.matcher(HttpMethod.valueOf(authorityUrl.getMethod()), authorityUrl.getUrl()).matches(request);
            }else {
                matches = builder.matcher(authorityUrl.getUrl()).matches(request);
            }
            if(matches){
                return true;
            }
        }
        return false;
    }

    // 生成缓存的 key（基于用户、请求路径、请求方法）
    private String generateCacheKey(Authentication authentication, String requestUri, String requestMethod) {
        return authentication.getName() + ":" + requestUri + ":" + requestMethod;
    }
}
