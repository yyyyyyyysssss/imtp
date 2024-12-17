package org.imtp.web.config.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.config.RequestUrlAuthority;
import org.imtp.web.domain.entity.User;
import org.imtp.web.filter.TokenAuthenticationFilter;
import org.imtp.web.utils.RSAUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
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
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.jackson2.WebServletJackson2Module;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.Principal;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/26 11:45
 */
@Configuration
@Slf4j
public class OAuth2AuthorizationServerConfig {


    @Resource
    private OidcUserInfoService oidcUserInfoService;

    @Resource
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @Resource
    private SecurityContextRepository securityContextRepository;

    //oauth2 服务器
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 配置默认的设置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        //自定义 /userinfo响应的内容
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            return oidcUserInfoService.loadUser(principal.getName());
        };

        http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
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
                });
        http
                // 当未登录时访问认证端点时重定向至login页面
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginTargetAuthenticationEntryPoint("http://localhost:3000/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                .securityContext(securityContext -> {
                    securityContext.securityContextRepository(securityContextRepository);
                })
                .addFilterBefore(tokenAuthenticationFilter, SecurityContextHolderFilter.class)
                .oauth2ResourceServer((resourceServer) -> {
                    resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
                    resourceServer.bearerTokenResolver(new OAuth2BearerTokenResolver());
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

    //自定义基于scope jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedScopeAuthoritiesConverter jwtGrantedScopeAuthoritiesConverter = new JwtGrantedScopeAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀
        jwtGrantedScopeAuthoritiesConverter.setAuthorityPrefix("");
        // 设置权限信息在jwt claims中的key
        jwtGrantedScopeAuthoritiesConverter.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedScopeAuthoritiesConverter);
        return jwtAuthenticationConverter;
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
        objectMapper.addMixIn(User.class,User.RequestUrlAuthorityMixin.class);

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
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) RSAUtil.loadLocalPublicKey();
        RSAPrivateKey privateKey = (RSAPrivateKey) RSAUtil.loadLocalPrivateKey();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("355cbc56f03da91b86306f3520186699")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://127.0.0.1:9090").build();
    }

}
