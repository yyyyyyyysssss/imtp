package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/11/19 13:09
 */
@Getter
@Setter
public class SwitchTenantDTO {

    @NotNull(message = "租户id不能为空")
    private Long tenantId;

}
