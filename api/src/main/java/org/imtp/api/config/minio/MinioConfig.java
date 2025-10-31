package org.imtp.api.config.minio;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/17 15:36
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
@Slf4j
public class MinioConfig {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    @Bean
    public MinioClient minioClient(){

        return MinioClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey,secretKey)
                .httpClient(okHttpClient())
                .build();
    }

    @Bean
    public MinioAsyncClient minioAsyncClient(){
        return MinioAsyncClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey,secretKey)
                .httpClient(okHttpClient())
                .build();
    }

    private OkHttpClient okHttpClient(){
        ConnectionPool connectionPool = new ConnectionPool(
                500,                       // 最大空闲连接数
                60,                        // 空闲连接的存活时间
                TimeUnit.SECONDS          // 空闲连接存活时间单位
        );
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)  // 设置连接超时
                .readTimeout(30, TimeUnit.SECONDS)     // 设置读取超时
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                .retryOnConnectionFailure(true)      // 连接失败时重试
                .build();
    }


}
