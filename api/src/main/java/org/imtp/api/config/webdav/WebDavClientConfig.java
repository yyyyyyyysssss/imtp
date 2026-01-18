package org.imtp.api.config.webdav;


import lombok.RequiredArgsConstructor;
import org.imtp.api.config.rest.HttpClientFactory;
import org.imtp.api.config.rest.RestClientFactory;
import org.imtp.api.utils.EncryptUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class WebDavClientConfig {

    private final HttpClientFactory httpClientFactory;

    private final RestClientFactory restClientFactory;

    private final WebDavProperties webDavProperties;

    @Bean
    public RestClient webDavRestClient(){

        return restClientFactory.create(
                httpClientFactory.create(false),
                webDavProperties.getEndpoint(),
                builder -> {}
        );
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        return "Basic " + EncryptUtils.base64Encode(token.getBytes(StandardCharsets.UTF_8));
    }

}
