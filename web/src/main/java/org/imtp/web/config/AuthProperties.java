package org.imtp.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/14 13:07
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private String loginPage;

    private Integer coexistToken;

    private KeyConfig jwt = new KeyConfig();

    private KeyConfig rememberMe = new KeyConfig();

    private List<RequestHeadAuthenticationConfig> requestHeadAuthentications = new ArrayList<>();

    @Setter
    @Getter
    public static class KeyConfig {

        private String secretKey;

        private Long expiration;

        private Long refreshExpiration;

        public void setExpiration(String expiration) {
            if (expiration.matches("\\d+")){
                this.expiration = Long.parseLong(expiration);
                return;
            }
            this.expiration = calculateExpiration(expiration);
        }

        public void setRefreshExpiration(String refreshExpiration) {
            if (refreshExpiration.matches("\\d+")){
                this.refreshExpiration = Long.parseLong(refreshExpiration);
                return;
            }
            this.refreshExpiration = calculateExpiration(refreshExpiration);
        }

        private Long calculateExpiration(String expiration){
            String unit = expiration.substring(expiration.length() - 1);
            String num = expiration.substring(0,expiration.length() - 1);
            long e;
            switch (unit) {
                case "s", "S":
                    e = Long.parseLong(num);
                    break;
                case "m", "M":
                    e = Long.parseLong(num) * 60;
                    break;
                case "h", "H":
                    e = Long.parseLong(num) * 60 * 60;
                    break;
                case "d", "D":
                    e = Long.parseLong(num) * 24 * 60 * 60;
                    break;
                default:
                    throw new UnsupportedOperationException("不支持的操作：" + unit);
            }
            return e;
        }
    }


    public String[] requestHeadAuthenticationPath(){
        return this.getRequestHeadAuthentications().stream().map(m -> m.antPath.split(",")).toList().stream().flatMap(Stream::of).toArray(String[]::new);
    }

    @Setter
    @Getter
    public static class RequestHeadAuthenticationConfig{

        private String antPath;

        private String apikey;


    }

}
