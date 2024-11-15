package org.imtp.server;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.ServerModel;
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

    private WebSocketConfig websocket = new WebSocketConfig();

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
        private ServerModel model;
        private String id;
    }

    @Getter
    @Setter
    class WebSocketConfig{

        private String host;

        private Integer port;
    }

}
