package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:42
 */
@Getter
@Setter
public class RoleQueryDTO extends PageQueryDTO{

    private String code;

    private String name;

    private Boolean enabled;

}
