package org.imtp.api.config.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import org.imtp.api.config.redis.RedisHelper;
import org.imtp.api.config.security.authentication.*;
import org.imtp.api.config.security.authentication.apikey.ApikeyAuthenticationProvider;
import org.imtp.api.config.security.authentication.apikey.SeparatorAntPathRequestMatcher;
import org.imtp.api.config.security.authentication.email.EmailAuthenticationProvider;
import org.imtp.api.config.security.authentication.ott.MagicLinkOneTimeTokenGenerationSuccessHandler;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshAuthenticationProvider;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshTokenAuthenticationFilter;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshTokenServices;
import org.imtp.api.config.security.authorization.PathVariableGuard;
import org.imtp.api.config.security.authorization.RequestPathAuthorizationManager;
import org.imtp.api.config.security.oauth2.JwtGrantedScopeAuthoritiesConverter;
import org.imtp.api.config.security.oauth2.OAuth2BearerTokenResolver;
import org.imtp.api.config.security.oauth2.OAuthClientAuthenticationProvider;
import org.imtp.api.service.LogoutService;
import org.imtp.api.utils.RSAUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ott.JdbcOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/13 21:41
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Resource
    private UserDetailsService userService;

    @Resource
    private LogoutService logoutService;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private RedisHelper redisHelper;

    @Resource
    private RedisTemplate<String, SecurityContext> authRedisTemplate;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private PathVariableGuard pathVariableGuard;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                //跨域支持
                .cors(cors -> cors.configurationSource(cs -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    return configuration;
                }))
                .anonymous(Customizer.withDefaults())
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                    exception.accessDeniedHandler(new CustomAccessDeniedEntryPoint());
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //身份认证信息存储
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(securityContextStore());
                })
                .authorizeHttpRequests(authorize -> {
                    //放行的路径
                    authorize
                            //允许所有人访问的路径
                            .requestMatchers(securityProperties.getAuthorize().getPermit().toArray(new String[0])).permitAll()
                            //允许所有异步请求
                            .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                            //只需要通过身份认证就能访问的路径
                            .requestMatchers(securityProperties.getAuthorize().getAuthenticated().toArray(new String[0])).authenticated()
                            //基于请求头apikey授权
                            .requestMatchers(securityProperties.requestHeadAuthenticationPath()).hasAuthority(ApikeyAuthenticationProvider.APIKEY_ROLE_CODE)
                            //基于用户id路径参数的授权
                            .requestMatchers("/api/social/*/{userId}")
                            .access((authentication, context) -> new AuthorizationDecision(
                                    pathVariableGuard.checkUserId(authentication.get(),context.getVariables().get("userId"))
                            ))
                            //必须校验权限的路径
                            .anyRequest().access(requestPathAuthorizationManager());
                })
                //记住我
                .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices()))
                //一次性令牌
                .oneTimeTokenLogin((ott) -> {
                    //生成一次性令牌的路径
                    ott.tokenGeneratingUrl("/ott/generate");
                    //登录处理路径
                    ott.loginProcessingUrl("/login/ott");
                    //禁用默认提交页面
                    ott.showDefaultSubmitPage(false);
                    //令牌生成以及存储
                    ott.tokenService(oneTimeTokenService());
                    //令牌生成成功处理器
                    ott.tokenGenerationSuccessHandler(new MagicLinkOneTimeTokenGenerationSuccessHandler(securityProperties.getLoginPage()));
                })
                //该过滤器解析token并校验通过后由SecurityContextHolderFilter过滤器加载SecurityContext
                .addFilterBefore(tokenAuthenticationFilter(tokenService(securityContextStore()),bearerTokenResolver()), SecurityContextHolderFilter.class)
                // 用于文件访问的过滤器
                .addFilterBefore(fileCookieAuthenticationFilter(tokenService(securityContextStore())), SecurityContextHolderFilter.class)
                //记住我过滤器
                .addFilterBefore(rememberMeFilter(authenticationManager(http),rememberMeServices()), UsernamePasswordAuthenticationFilter.class)
                //刷新token过滤器
                .addFilterAfter(refreshTokenAuthenticationFilter(authenticationManager(http),bearerTokenResolver(),refreshTokenServices(tokenService(securityContextStore()))), UsernamePasswordAuthenticationFilter.class)
                //基于请求头apikey认证的过滤器
                .addFilterBefore(apikeyAuthenticationFilter(authenticationManager(http)), HeaderWriterFilter.class)
                //登出过滤器
                .addFilterAfter(logoutFilter(), AuthorizationFilter.class)
                .logout(AbstractHttpConfigurer::disable)
                // oauth2资源服务器
                .oauth2ResourceServer((resourceServer) -> {
                    resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
                    resourceServer.bearerTokenResolver(oAuth2BearerTokenResolver());
                });
        return http.build();
    }

    //身份认证管理器
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                //用户名密码身份认证
                .authenticationProvider(daoAuthenticationProvider())
                //邮箱验证码认证
                .authenticationProvider(emailAuthenticationProvider())
                //用于使用三方登录的身份认证
                .authenticationProvider(oAuthClientAuthenticationProvider())
                //记住我身份认证
                .authenticationProvider(rememberMeAuthenticationProvider())
                //一次性令牌认证
                .authenticationProvider(oneTimeTokenAuthenticationProvider())
                //刷新token
                .authenticationProvider(refreshAuthenticationProvider())
                //基于apikey认证
                .authenticationProvider(apikeyAuthenticationProvider())
                .parentAuthenticationManager(null)
                .build();
    }

    //基于用户名密码认证
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        // 密码防暴力破解登录
        LoginAttemptService loginAttemptService = new LoginAttemptService(redisHelper);
        DaoAuthenticationProvider authProvider = new UsernamePasswordAuthenticationProvider(userService,loginAttemptService);
        // 设置密码编辑器
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    //三方登录认证
    @Bean
    public EmailAuthenticationProvider emailAuthenticationProvider() {
        return new EmailAuthenticationProvider(userService, redisHelper);
    }

    //三方登录认证
    @Bean
    public OAuthClientAuthenticationProvider oAuthClientAuthenticationProvider() {
        OAuthClientAuthenticationProvider oAuthClientAuthenticationProvider = new OAuthClientAuthenticationProvider();
        oAuthClientAuthenticationProvider.setUserDetailsService(userService);
        return oAuthClientAuthenticationProvider;
    }

    //基于请求路径的权限管理器
    @Bean
    public RequestPathAuthorizationManager requestPathAuthorizationManager() {

        return new RequestPathAuthorizationManager(permissionCache());
    }

    @Bean
    public FileCookieAuthenticationFilter fileCookieAuthenticationFilter(TokenService tokenService){

        return new FileCookieAuthenticationFilter(tokenService);
    }

    //token过滤器
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenService tokenService ,BearerTokenResolver bearerTokenResolver) {

        return new TokenAuthenticationFilter(bearerTokenResolver, tokenService);
    }

    //刷新token
    @Bean
    public RefreshTokenServices refreshTokenServices(TokenService tokenService){
        return new RefreshTokenServices(tokenService);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter(
            AuthenticationManager authenticationManager,
            BearerTokenResolver bearerTokenResolver,
            RefreshTokenServices refreshTokenServices) {

        return new RefreshTokenAuthenticationFilter(authenticationManager,bearerTokenResolver, refreshTokenServices);
    }
    @Bean
    public RefreshAuthenticationProvider refreshAuthenticationProvider(){
        return new RefreshAuthenticationProvider(userService);
    }

    //基于请求头apikey的认证过滤器
    @Bean
    public RequestHeaderAuthenticationFilter apikeyAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        String[] antPaths = securityProperties.requestHeadAuthenticationPath();
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("apikey");
        requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);
        requestHeaderAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new SeparatorAntPathRequestMatcher(antPaths));
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return requestHeaderAuthenticationFilter;
    }
    @Bean
    public ApikeyAuthenticationProvider apikeyAuthenticationProvider() {

        return new ApikeyAuthenticationProvider(securityProperties.getRequestHeadAuthentications());
    }

    //token解析器
    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        return new NormalBearerTokenResolver();
    }

    //用户SecurityContext存储
    @Bean
    public SecurityContextStore securityContextStore() {

        return new RedisSecurityContextRepository(authRedisTemplate,securityProperties);
    }

    @Bean
    public TokenService tokenService(SecurityContextStore securityContextStore){

        return new JWTTokenService(redisHelper,securityProperties,securityContextStore);
    }

    //登出过滤器
    @Bean
    public LogoutFilter logoutFilter() {

        return new LogoutFilter(new CustomLogoutSuccessHandler(permissionCache()),logoutService);
    }

    // 用于缓存权限校验的结果
    @Bean
    public Cache<String, Boolean> permissionCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(50000)
                .build();
    }

    //密码加密  调试使用 生产环境使用BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //记住我
    @Bean
    public RememberMeAuthenticationFilter rememberMeFilter(AuthenticationManager authenticationManager,RememberMeServices rememberMeServices) {

        return new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices);
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        String secretKey = securityProperties.getRememberMe().getSecretKey();
        return new TokenBasedRememberMeServices(secretKey, userService, TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256);
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        String secretKey = securityProperties.getRememberMe().getSecretKey();
        return new RememberMeAuthenticationProvider(secretKey);
    }

    //一次性令牌
    //使用数据库存储
    @Bean
    public OneTimeTokenService oneTimeTokenService() {
        return new JdbcOneTimeTokenService(jdbcTemplate);
    }

    @Bean
    public OneTimeTokenAuthenticationProvider oneTimeTokenAuthenticationProvider() {
        return new OneTimeTokenAuthenticationProvider(oneTimeTokenService(),userService);
    }


    //  oauth2 资源服务器

    @Bean
    public OAuth2BearerTokenResolver oAuth2BearerTokenResolver(){

        return new OAuth2BearerTokenResolver();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        //自定义基于scope jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
        JwtGrantedScopeAuthoritiesConverter jwtGrantedScopeAuthoritiesConverter = new JwtGrantedScopeAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀
        jwtGrantedScopeAuthoritiesConverter.setAuthorityPrefix("");

        // 设置权限信息在jwt claims中的key
        jwtGrantedScopeAuthoritiesConverter.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedScopeAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource() throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) RSAUtils.loadLocalPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) RSAUtils.loadLocalPrivateKey();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("355cbc56f03da91b86306f3520186699")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

}
