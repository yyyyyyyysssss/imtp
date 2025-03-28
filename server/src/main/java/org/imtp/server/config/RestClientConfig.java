package org.imtp.server.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.Timeout;
import org.imtp.server.restclient.ChatApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/28 9:07
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "http.config")
@Slf4j
@Configuration
public class RestClientConfig {

    private Long connectionRequestTimeout;

    private Long responseTimeout;

    @Value("${im.chat.apikey}")
    private String apikey;

    @Value("${im.chat.url}")
    private String chatApiBaseUrl;

    @Bean
    public ChatApi chatApi() {
        RestClient restClient = chatRestClient();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(ChatApi.class);
    }

    @Bean
    public RestClient chatRestClient() {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    log.error("request error code: {}",res.getStatusCode().value());
                })
                .baseUrl(chatApiBaseUrl)
                .defaultHeader("apikey", apikey)
                .build();
    }


    public ClientHttpRequestFactory clientHttpRequestFactory() {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.SECONDS)
                .setResponseTimeout(responseTimeout, TimeUnit.SECONDS)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients
                .custom()
                //请求级别的限制
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
        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
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
