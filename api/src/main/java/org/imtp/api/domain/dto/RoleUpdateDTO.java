package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:39
 */
@Getter
@Setter
public class RoleUpdateDTO {

    @NotBlank(message = "角色ID不能为空")
    private String id;

    private String code;

    private String name;

    private Boolean enabled;

    private List<Long> userIds;

    private List<Long> authorityIds;

}
