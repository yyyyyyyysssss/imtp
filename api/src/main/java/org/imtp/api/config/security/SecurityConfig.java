package org.imtp.api.config.security;

import jakarta.annotation.Resource;
import org.imtp.api.config.redis.RedisWrapper;
import org.imtp.api.config.security.authentication.CustomAccessDeniedEntryPoint;
import org.imtp.api.config.security.authentication.CustomAuthenticationEntryPoint;
import org.imtp.api.config.security.authentication.NormalBearerTokenResolver;
import org.imtp.api.config.security.authentication.TokenAuthenticationFilter;
import org.imtp.api.config.security.authentication.apikey.ApikeyAuthenticationProvider;
import org.imtp.api.config.security.authentication.apikey.SeparatorAntPathRequestMatcher;
import org.imtp.api.config.security.authentication.email.EmailAuthenticationProvider;
import org.imtp.api.config.security.authentication.ott.MagicLinkOneTimeTokenGenerationSuccessHandler;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshAuthenticationProvider;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshTokenAuthenticationFilter;
import org.imtp.api.config.security.authentication.refreshtoken.RefreshTokenServices;
import org.imtp.api.config.security.authorization.PathVariableGuard;
import org.imtp.api.config.security.authorization.RequestPathAuthorizationManager;
import org.imtp.api.config.security.oauth2.OAuthClientAuthenticationProvider;
import org.imtp.api.service.LogoutService;
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
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

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
    private AuthProperties authProperties;

    @Resource
    private RedisWrapper redisWrapper;

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
                    securityContext.securityContextRepository(securityContextRepository());
                })
                .authorizeHttpRequests(authorize -> {
                    //放行的路径
                    authorize
                            //允许所有人访问的路径
                            .requestMatchers(authProperties.getAuthorize().getPermit().toArray(new String[0])).permitAll()
                            //只需要通过身份认证就能访问的路径
                            .requestMatchers(authProperties.getAuthorize().getAuthenticated().toArray(new String[0])).authenticated()
                            //基于请求头apikey授权
                            .requestMatchers(authProperties.requestHeadAuthenticationPath()).hasAuthority("request_header")
                            //基于用户id路径参数的授权
                            .requestMatchers("/social/*/{userId}")
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
                    ott.tokenGenerationSuccessHandler(new MagicLinkOneTimeTokenGenerationSuccessHandler(authProperties.getLoginPage()));
                })
                //该过滤器解析token并校验通过后由SecurityContextHolderFilter过滤器加载SecurityContext
                .addFilterBefore(tokenAuthenticationFilter(tokenService()), SecurityContextHolderFilter.class)
                //记住我过滤器
                .addFilterBefore(rememberMeFilter(http), UsernamePasswordAuthenticationFilter.class)
                //刷新token过滤器
                .addFilterAfter(refreshTokenAuthenticationFilter(http,tokenService()), UsernamePasswordAuthenticationFilter.class)
                //基于请求头apikey认证的过滤器
                .addFilterBefore(apikeyAuthenticationFilter(http), HeaderWriterFilter.class)
                //登出过滤器
                .addFilterAfter(logoutFilter(bearerTokenResolver(),tokenService()), AuthorizationFilter.class)
                .logout(AbstractHttpConfigurer::disable);
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
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        // 设置密码编辑器
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    //三方登录认证
    @Bean
    public EmailAuthenticationProvider emailAuthenticationProvider() {
        return new EmailAuthenticationProvider(userService, redisWrapper);
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

        return new RequestPathAuthorizationManager();
    }

    //token过滤器
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenService tokenService) {

        return new TokenAuthenticationFilter(bearerTokenResolver(), tokenService);
    }

    //刷新token
    @Bean
    public RefreshTokenServices refreshTokenServices(TokenService tokenService){
        return new RefreshTokenServices(tokenService);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter(HttpSecurity http,TokenService tokenService) throws Exception {

        return new RefreshTokenAuthenticationFilter(authenticationManager(http),bearerTokenResolver(), refreshTokenServices(tokenService));
    }
    @Bean
    public RefreshAuthenticationProvider refreshAuthenticationProvider(){
        return new RefreshAuthenticationProvider(userService);
    }

    //基于请求头apikey的认证过滤器
    @Bean
    public RequestHeaderAuthenticationFilter apikeyAuthenticationFilter(HttpSecurity http) throws Exception {
        String[] antPaths = authProperties.requestHeadAuthenticationPath();
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("apikey");
        requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);
        requestHeaderAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new SeparatorAntPathRequestMatcher(antPaths));
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager(http));
        return requestHeaderAuthenticationFilter;
    }
    @Bean
    public ApikeyAuthenticationProvider apikeyAuthenticationProvider() {

        return new ApikeyAuthenticationProvider(authProperties.getRequestHeadAuthentications());
    }

    //token解析器
    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        return new NormalBearerTokenResolver();
    }

    //用户SecurityContext存储
    @Bean
    public SecurityContextRepository securityContextRepository() {

        return new RedisSecurityContextRepository(authRedisTemplate,authProperties);
    }

    @Bean
    public TokenService tokenService(){

        return new JWTTokenService(redisWrapper,authProperties,securityContextRepository());
    }

    //登出过滤器
    @Bean
    public LogoutFilter logoutFilter(BearerTokenResolver bearerTokenResolver,TokenService tokenService) {

        return new LogoutFilter((req, res, auth) -> {
        }, logoutHandler(bearerTokenResolver,tokenService),logoutService);
    }
    @Bean
    public LogoutHandler logoutHandler(BearerTokenResolver bearerTokenResolver,TokenService tokenService){

        return (request, response, authentication) -> {
            //提取token
            String token = bearerTokenResolver.resolve(request);
            //将token过期
            tokenService.revokeToken(token);
        };
    }

    //密码加密  调试使用 生产环境使用BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //记住我
    @Bean
    public RememberMeAuthenticationFilter rememberMeFilter(HttpSecurity http) throws Exception {

        return new RememberMeAuthenticationFilter(authenticationManager(http), rememberMeServices());
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        String secretKey = authProperties.getRememberMe().getSecretKey();
        return new TokenBasedRememberMeServices(secretKey, userService, TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256);
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        String secretKey = authProperties.getRememberMe().getSecretKey();
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

}
