package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.domain.validation.ValidApiUrls;
import org.imtp.api.enums.AuthorityType;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:15
 */
@Getter
@Setter
public class AuthorityUpdateDTO {

    @NotBlank(message = "权限ID不能为空")
    private String id;

    private String parentId;

    private String rootId;

    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotBlank(message = "资源路径不能为空")
    @ValidApiUrls(message = "资源路径不合法")
    private String urls;

    private Integer sort;

}
