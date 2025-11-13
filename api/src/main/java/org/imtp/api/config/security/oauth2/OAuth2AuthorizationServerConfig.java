package org.imtp.api.config.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.security.AuthProperties;
import org.imtp.api.config.security.RequestUrlAuthority;
import org.imtp.api.config.security.SecurityContextStore;
import org.imtp.api.config.security.authentication.TokenAuthenticationFilter;
import org.imtp.api.domain.entity.AuthorityUrl;
import org.imtp.api.domain.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.jackson2.WebServletJackson2Module;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/26 11:45
 */
@EnableWebSecurity
@Configuration
@Slf4j
public class OAuth2AuthorizationServerConfig {


    @Resource
    private OidcUserInfoService oidcUserInfoService;

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Resource
    private SecurityContextStore securityContextStore;

    @Resource
    private AuthProperties authProperties;

    @Resource
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Resource
    private OAuth2BearerTokenResolver oAuth2BearerTokenResolver;

    //oauth2 服务器
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置默认的设置
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        //自定义 /userinfo响应的内容
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            return oidcUserInfoService.loadUser(principal.getName());
        };
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc((oidc) -> {
                                    oidc.userInfoEndpoint((userInfo) -> userInfo.userInfoMapper(userInfoMapper));
                                })
                                .authorizationEndpoint(authorizationEndpoint -> {
                                    authorizationEndpoint.consentPage("/oauth2/consent?type=code");
                                })
                                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint -> {
                                    deviceAuthorizationEndpoint.verificationUri("/oauth2/activate");
                                })
                                .deviceVerificationEndpoint(deviceVerificationEndpoint -> {
                                    deviceVerificationEndpoint.consentPage("/oauth2/consent?type=device");
                                    deviceVerificationEndpoint.deviceVerificationResponseHandler(new SimpleUrlAuthenticationSuccessHandler("/activated"));
                                })
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                )
                // 当未登录时访问认证端点时重定向至login页面
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginTargetAuthenticationEntryPoint(authProperties.getLoginPage()),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(securityContextStore);
                })
                .addFilterBefore(tokenAuthenticationFilter, SecurityContextHolderFilter.class)
                // oauth2资源服务器
                .oauth2ResourceServer((resourceServer) -> {
                    resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter));
                    resourceServer.bearerTokenResolver(oAuth2BearerTokenResolver);
                });

        return http.build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
        return context -> {
            String name = context.getPrincipal().getName();
            //自定义id_token中包含的信息
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                OidcUserInfo oidcUserInfo = oidcUserInfoService.loadUser(name);
                context.getClaims().claims(claims -> claims.putAll(oidcUserInfo.getClaims()));
            }
        };
    }

    // 注册客户端应用, 对应 oauth2_registered_client 表
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    // 令牌的发放记录, 对应 oauth2_authorization 表
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);

        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper authorizationRowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
                registeredClientRepository);
        authorizationRowMapper.setLobHandler(new DefaultLobHandler());

        //spring security 反序列化设置
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new CoreJackson2Module());
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new WebServletJackson2Module());
        objectMapper.addMixIn(RequestUrlAuthority.class, RequestUrlAuthority.RequestUrlAuthorityMixin.class);
        objectMapper.addMixIn(AuthorityUrl.class, AuthorityUrl.AuthorityUrlMixin.class);
        objectMapper.addMixIn(User.class, User.UserMixin.class);

        authorizationRowMapper.setObjectMapper(objectMapper);

        jdbcOAuth2AuthorizationService.setAuthorizationRowMapper(authorizationRowMapper);

        return jdbcOAuth2AuthorizationService;
    }

    // 把资源拥有者授权确认操作保存到数据库, 对应 oauth2_authorization_consent 表
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://127.0.0.1:9090").build();
    }

}
