package org.imtp.web.config.zookeeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 15:14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInfo {

    private String host;

    private Integer port;

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
