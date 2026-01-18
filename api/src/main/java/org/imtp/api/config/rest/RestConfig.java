package org.imtp.api.config.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/2 21:13
 */
@Getter
@Setter
@Configuration
@Slf4j
public class RestConfig {

    @Bean(name = "oauth2ClientRestTemplate")
    public RestTemplate oauth2ClientRestTemplate(HttpClientFactory httpClientFactory) {
        return new RestTemplate(httpClientFactory.create(true));
    }

}
