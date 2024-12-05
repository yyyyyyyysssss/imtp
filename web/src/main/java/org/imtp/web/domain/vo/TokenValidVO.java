package org.imtp.web.domain.vo;

import org.imtp.web.domain.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenValidVO {

    private Boolean active;

    private User userInfo;
    
}
