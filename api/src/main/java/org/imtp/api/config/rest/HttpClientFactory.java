package org.imtp.api.config.rest;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.imtp.common.config.constant.CommonConstant;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HttpClientFactory {

    private final HttpClientProperties httpClientProperties;

    private final PoolingHttpClientConnectionManager connectionManager;

    private volatile CloseableHttpClient defaultClient;

    private volatile CloseableHttpClient proxyClient;

    public HttpClientFactory(HttpClientProperties httpClientProperties){
        this.httpClientProperties = httpClientProperties;
        this.connectionManager = buildConnectionManager();
    }

    public ClientHttpRequestFactory create(boolean enableProxy) {
        try {
            CloseableHttpClient client = enableProxy ? getProxyClient() : getDefaultClient();
            return new HttpComponentsClientHttpRequestFactory(client);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private CloseableHttpClient getDefaultClient() throws URISyntaxException {
        if (defaultClient == null) {
            synchronized (this) {
                if (defaultClient == null) {
                    defaultClient = buildHttpClient(false);
                }
            }
        }
        return defaultClient;
    }

    private CloseableHttpClient getProxyClient() throws URISyntaxException {
        if (proxyClient == null) {
            synchronized (this) {
                if (proxyClient == null) {
                    proxyClient = buildHttpClient(true);
                }
            }
        }
        return proxyClient;
    }

    private CloseableHttpClient buildHttpClient(boolean enableProxy) throws URISyntaxException {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout(), TimeUnit.SECONDS)
                .setResponseTimeout(httpClientProperties.getResponseTimeout(), TimeUnit.SECONDS)
                .build();
        HttpClientBuilder httpClientBuilder = HttpClients
                .custom()
                .addRequestInterceptorFirst(new HttpClientTraceIdInterceptor())
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager);
        String proxy = httpClientProperties.getProxy();
        if (enableProxy && proxy != null && !proxy.isEmpty()) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(HttpHost.create(proxy)));
        }
        return httpClientBuilder.build();
    }

    private PoolingHttpClientConnectionManager buildConnectionManager(){
        ClientTlsStrategyBuilder tlsBuilder = ClientTlsStrategyBuilder.create();
        if (httpClientProperties.isInsecureSkipVerify()) {
            log.warn("HTTP CLIENT RUNNING IN INSECURE MODE (SKIP TLS VERIFY) — FOR TEST ENV ONLY");
            tlsBuilder
                    .setSslContext(createSSLContext())
                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        } else {
            // 使用 JVM 默认 trustStore（企业级标准）
            tlsBuilder.setSslContext(SSLContexts.createSystemDefault());
        }
        return PoolingHttpClientConnectionManagerBuilder
                .create()
                //所有路由最大连接数
                .setMaxConnTotal(httpClientProperties.getMaxConnTotal())
                //单路由并发连接数
                .setMaxConnPerRoute(httpClientProperties.getMaxConnPerRoute())
                .setTlsSocketStrategy(tlsBuilder.buildClassic())
                // LAX:  高负载下可突破每个路由的最大连接数限制，并且不强制执行总最大连接数限制，
                //       以吞吐优先，可能导致连接数失控，引发资源耗尽和雪崩风险（适用于压测场景）
                //
                // STRICT: 严格遵守每个路由最大连接数和总最大连接数限制，
                //         当连接池耗尽时请求将阻塞等待或快速失败，
                //         通过背压机制保护系统稳定性，防止线程、连接和内存失控（生产环境推荐）
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                //连接池重用策略 LIFO重用尽可能少的连接，使连接有可能变为空闲和过期；FIFO平等地重用所有连接，防止它们变为空闲和过期
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                //连接本身限制配置
                .setDefaultConnectionConfig(
                        ConnectionConfig.custom()
                                //建立连接最大等待时长
                                .setConnectTimeout(Timeout.ofSeconds(30))
                                .setSocketTimeout(Timeout.ofSeconds(httpClientProperties.getResponseTimeout()))
                                .build()
                )
                .build();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down HttpClient connection pool");
        connectionManager.close();
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

    private static SSLContext createSSLContext() {
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
