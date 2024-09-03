package org.imtp.web.config.minio;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .build();
    }


}
