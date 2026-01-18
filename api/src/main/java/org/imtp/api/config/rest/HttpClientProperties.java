package org.imtp.api.config.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "http.config")
@Configuration
@Getter
@Setter
public class HttpClientProperties {

    private long connectionRequestTimeout;

    private long responseTimeout;

    private int maxConnTotal;

    private int maxConnPerRoute;

    private boolean insecureSkipVerify;

    private String proxy;

}
