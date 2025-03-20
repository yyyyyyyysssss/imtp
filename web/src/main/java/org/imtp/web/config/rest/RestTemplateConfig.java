package org.imtp.web.config.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
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

    private Long connectTimeout;

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

        HttpClientBuilder httpClientBuilder  = null;
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
                                    .setSSLSocketFactory(
                                            SSLConnectionSocketFactoryBuilder
                                                    .create()
                                                    .setSslContext(createSSLContext())
                                                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                                    .build()
                                    )
                                    .build()
                    );
            if (proxy != null && !proxy.isEmpty()){
                httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(HttpHost.create(proxy)));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
    }

    private static class HttpClientTraceIdInterceptor implements HttpRequestInterceptor{

        @Override
        public void process(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext) {
            String tranceId = MDC.get(CommonConstant.TRACE_ID);
            if (tranceId != null){
                httpRequest.addHeader(CommonConstant.TRACE_ID,tranceId);
            }
        }
    }

    private SSLContext createSSLContext(){
        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, new TrustManager[]{new TrustAllCerts()}, null);
            return tls;
        } catch (Exception e) {
            log.error("create SSLContext error:{}", e.toString());
            throw new RuntimeException("create SSLContext error",e);
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
