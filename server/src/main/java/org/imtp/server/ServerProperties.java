package org.imtp.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/29 14:59
 */
@Component
@ConfigurationProperties(prefix = "im.server")
@Getter
@Setter
public class ServerProperties {

    private String host;

    private Integer port;

}
