package org.imtp.web.config;

import jakarta.annotation.Resource;
import org.imtp.web.filter.RefreshTokenAuthenticationFilter;
import org.imtp.web.filter.TokenAuthenticationFilter;
import org.imtp.web.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

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

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
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
                    authorize.requestMatchers(
                                    "/favicon.ico",
                                    "/login"
                            )
                            .permitAll()
                            .requestMatchers("/refreshToken").hasAuthority("refresh_token")
                            .anyRequest()
                            .access(requestPathAuthorizationManager());
                })
                .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices()))
                //该过滤器解析token并校验通过后由SecurityContextHolderFilter过滤器加载SecurityContext
                .addFilterBefore(tokenAuthenticationFilter(), SecurityContextHolderFilter.class)
                .addFilterBefore(rememberMeFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(refreshTokenAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
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
                //记住我身份认证
                .authenticationProvider(rememberMeAuthenticationProvider())
                .parentAuthenticationManager(null)
                .build();
    }

    //基于用户名密码身份认证
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        // 设置密码编辑器
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    //基于请求路径的权限管理器
    @Bean
    public RequestPathAuthorizationManager requestPathAuthorizationManager() {

        return new RequestPathAuthorizationManager();
    }

    //token过滤器
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {

        return new TokenAuthenticationFilter(bearerTokenResolver(),tokenService);
    }

    @Bean
    public RefreshTokenAuthenticationFilter refreshTokenAuthenticationFilter() {

        return new RefreshTokenAuthenticationFilter(bearerTokenResolver(),tokenService);
    }


    //token解析器
    @Bean
    public BearerTokenResolver bearerTokenResolver(){

        return new NormalBearerTokenResolver();
    }

    //用户SecurityContext存储
    @Bean
    public SecurityContextRepository securityContextRepository(){

        return new RedisSecurityContextRepository();
    }

    //登出过滤器
    @Bean
    public LogoutFilter logoutFilter(){

        return new LogoutFilter((req,res,auth) -> {},logoutHandler);
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
