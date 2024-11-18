package org.imtp.web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/17 20:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private String accessTokenId;

    private String refreshToken;

    private Long expiration;

}
