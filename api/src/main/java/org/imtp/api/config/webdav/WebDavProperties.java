package org.imtp.api.config.webdav;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "webdav")
@Getter
@Setter
@Slf4j
public class WebDavProperties {

    private String endpoint;

    private String username;

    private String password;



}
