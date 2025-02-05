package org.imtp.web.config;

import jakarta.annotation.Resource;
import org.imtp.web.config.oauth2.OAuthClientAuthenticationProvider;
import org.imtp.web.filter.RefreshTokenAuthenticationFilter;
import org.imtp.web.filter.TokenAuthenticationFilter;
import org.imtp.web.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private HttpSecurity http;

    @Resource
    private LogoutHandler logoutHandler;

    @Resource
    private TokenService tokenService;

    @Resource
    private AuthProperties authProperties;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(securityContextRepository());
                })
                .authorizeHttpRequests(authorize -> {
                    //放行的路径
                    authorize.requestMatchers(
                                    "/login",
                                    "/refreshToken",
                                    "/sendEmailVerificationCode",
                                    "/error",
                                    "/assets/**",
                                    "/favicon.ico",
                                    "/oauth2/client/**",
                                    "/oauth2/consent",
                                    "/oauth2/activate",
                                    "/activated",
                                    "/open/**"
                            )
                            .permitAll()
                            //只需要通过身份认证就能访问的路径
                            .requestMatchers(
                                    "/logout",
                                    "/file/**",
                                    "/service/discovery",
                                    "/social/**"
                            ).authenticated()
                            //基于请求头授权
                            .requestMatchers(authProperties.requestHeadAuthenticationPath()).hasAuthority("request_header")
                            //必须校验权限的路径
                            .anyRequest()
                            .access(requestPathAuthorizationManager());
                })
                .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices()))
                //该过滤器解析token并校验通过后由SecurityContextHolderFilter过滤器加载SecurityContext
                .addFilterBefore(tokenAuthenticationFilter(), SecurityContextHolderFilter.class)
                .addFilterBefore(rememberMeFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(refreshTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                //基于请求头的认证
                .addFilterBefore(requestHeaderAuthenticationFilter(), HeaderWriterFilter.class)
                .addFilterAfter(logoutFilter(), AuthorizationFilter.class)
                .logout(AbstractHttpConfigurer::disable);
        return http.build();
    }

    //身份认证管理器
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                //用户名密码身份认证
                .authenticationProvider(daoAuthenticationProvider())
                //邮箱验证码认证
                .authenticationProvider(emailAuthenticationProvider())
                //用于使用三方登录的身份认证
                .authenticationProvider(oAuthClientAuthenticationProvider())
                //记住我身份认证
                .authenticationProvider(rememberMeAuthenticationProvider())
                //刷新token
                .authenticationProvider(refreshAuthenticationProvider())
                //基于请求头secret认证
                .authenticationProvider(requestHeaderAuthenticationProvider())
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
        return new EmailAuthenticationProvider(userService, redisTemplate);
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
    public TokenAuthenticationFilter tokenAuthenticationFilter() {

        return new TokenAuthenticationFilter(bearerTokenResolver(), tokenService);
    }

    @Bean
    public RefreshTokenServices refreshTokenServices(){
        return new RefreshTokenServices(tokenService);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter() throws Exception {

        return new RefreshTokenAuthenticationFilter(authenticationManager(),bearerTokenResolver(), refreshTokenServices());
    }

    @Bean
    public RefreshAuthenticationProvider refreshAuthenticationProvider(){
        return new RefreshAuthenticationProvider(userService);
    }

    //基于请求头的认证
    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() throws Exception {
        String[] antPaths = authProperties.requestHeadAuthenticationPath();
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("apikey");
        requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);
        requestHeaderAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new SeparatorAntPathRequestMatcher(antPaths));
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return requestHeaderAuthenticationFilter;
    }

    @Bean
    public RequestHeaderAuthenticationProvider requestHeaderAuthenticationProvider() {

        return new RequestHeaderAuthenticationProvider(authProperties.getRequestHeadAuthentications());
    }

    //token解析器
    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        return new NormalBearerTokenResolver();
    }

    //用户SecurityContext存储
    @Bean
    public SecurityContextRepository securityContextRepository() {

        return new RedisSecurityContextRepository();
    }

    //登出过滤器
    @Bean
    public LogoutFilter logoutFilter() {

        return new LogoutFilter((req, res, auth) -> {
        }, logoutHandler);
    }

    //密码加密  调试使用 生产环境使用BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    //记住我
    @Bean
    public RememberMeAuthenticationFilter rememberMeFilter() throws Exception {

        return new RememberMeAuthenticationFilter(authenticationManager(), rememberMeServices());
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

}
