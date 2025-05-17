package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:15
 */
@Getter
@Setter
public class AuthorityUpdateDTO extends AuthorityAddDTO{

    @NotBlank(message = "权限ID不能为空")
    private String id;

}
