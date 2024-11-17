package org.imtp.web.domain.dto;

import lombok.Data;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/17 20:13
 */
@Data
public class TokenDTO {

    private String accessTokenId;

    private String refreshTokenId;

    private Long expiration;

}
