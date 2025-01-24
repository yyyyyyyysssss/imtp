package org.imtp.web.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.common.enums.ClientType;

@Getter
@Setter
public class TokenValidVO {

    private Boolean active;

    private String subject;

    private ClientType clientType;

    private Long expiration;
    
}
