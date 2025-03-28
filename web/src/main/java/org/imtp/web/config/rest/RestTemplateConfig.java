package org.imtp.web.config.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.Timeout;
import org.imtp.web.config.constant.CommonConstant;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/2 21:13
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "http.config")
@Configuration
@Slf4j
public class RestTemplateConfig {

    private Long connectionRequestTimeout;

    private Long responseTimeout;

    private String proxy;

    @Bean(name = "oauth2ClientRestTemplate")
    public RestTemplate oauth2ClientRestTemplate() {
        return createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.SECONDS)
                .setResponseTimeout(responseTimeout, TimeUnit.SECONDS)
                .build();

        HttpClientBuilder httpClientBuilder = null;
        try {
            httpClientBuilder = HttpClients
                    .custom()
                    .addRequestInterceptorFirst(new HttpClientTraceIdInterceptor())
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(
                            PoolingHttpClientConnectionManagerBuilder
                                    .create()
                                    //所有路由最大连接数
                                    .setMaxConnTotal(2000)
                                    //单路由并发连接数
                                    .setMaxConnPerRoute(100)
                                    .setTlsSocketStrategy(
                                            (TlsSocketStrategy) ClientTlsStrategyBuilder.create()
                                                    .setSslContext(createSSLContext())
                                                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                                    .build()
                                    )
                                    //使用LAX策略 HttpClient 可以在高负载下超过每个路由的最大限制，并且不强制执行总最大限制
                                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)
                                    //连接池重用策略 LIFO重用尽可能少的连接，使连接有可能变为空闲和过期；FIFO平等地重用所有连接，防止它们变为空闲和过期
                                    .setConnPoolPolicy(PoolReusePolicy.LIFO)
                                    //连接本身限制配置
                                    .setDefaultConnectionConfig(
                                            ConnectionConfig.custom()
                                                    //建立连接最大等待时长
                                                    .setConnectTimeout(Timeout.ofMinutes(1))
                                                    .build()
                                    )
                                    .build()
                    );
            if (proxy != null && !proxy.isEmpty()) {
                httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(HttpHost.create(proxy)));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
    }

    private static class HttpClientTraceIdInterceptor implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext) {
            String tranceId = MDC.get(CommonConstant.TRACE_ID);
            if (tranceId != null) {
                httpRequest.addHeader(CommonConstant.TRACE_ID, tranceId);
            }
        }
    }

    private SSLContext createSSLContext() {
        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, new TrustManager[]{new TrustAllCerts()}, null);
            return tls;
        } catch (Exception e) {
            log.error("create SSLContext error:{}", e.toString());
            throw new RuntimeException("create SSLContext error", e);
        }
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
