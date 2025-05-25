package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.AuthorityType;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 10:45
 */
@Getter
@Setter
public class MenuUpdateDTO {

    @NotBlank(message = "菜单ID不能为空")
    private String id;

    private String parentId;

    private String rootId;

    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @NotBlank(message = "菜单路由不能为空")
    private String routePath;

    private String icon;

    private Integer sort;

}
