package org.imtp.server;

import lombok.Getter;
import lombok.Setter;
import org.imtp.server.enums.Model;
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

    private Configuration configuration = new Configuration();

    @Override
    public String toString() {
        return "ServerProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Getter
    @Setter
    class Configuration{
        private Model model;
        private String id;
    }

}
