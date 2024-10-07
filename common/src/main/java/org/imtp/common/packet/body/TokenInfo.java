package org.imtp.common.packet.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.ClientType;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/21 18:12
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenInfo {

    private Long userId;

    private String accessToken;

    private String refreshToken;

    private String rememberMeToken;

    private ClientType clientType;

    private Long expiration;

}
