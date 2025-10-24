package org.imtp.api.domain.dto;


import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.TenantStatus;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:29
 */
@Getter
@Setter
public class TenantQueryDTO extends PageQueryDTO{

    private String keyword;

    private TenantStatus status;

}
