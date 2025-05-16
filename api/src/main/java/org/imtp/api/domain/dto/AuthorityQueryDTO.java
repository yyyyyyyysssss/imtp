package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.AuthorityType;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:15
 */
@Getter
@Setter
public class AuthorityQueryDTO extends PageQueryDTO{

    private String code;

    private String name;

    private AuthorityType type;


}
