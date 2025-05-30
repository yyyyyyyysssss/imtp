package org.imtp.api.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.domain.entity.AuthorityUrl;
import org.imtp.api.domain.validation.ValidApiUrls;
import org.imtp.api.enums.AuthorityType;

import java.util.List;

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

    private String name;

    @Valid
    private List<AuthorityUrl> urls;

    private Integer sort;

}
