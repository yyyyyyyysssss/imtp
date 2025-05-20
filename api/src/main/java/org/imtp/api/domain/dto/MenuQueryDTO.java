package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 17:30
 */
@Getter
@Setter
public class MenuQueryDTO extends PageQueryDTO{

    private String code;

    private String name;

    private String routePath;


}
