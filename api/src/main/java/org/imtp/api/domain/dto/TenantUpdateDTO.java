package org.imtp.api.domain.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.TenantStatus;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:29
 */
@Getter
@Setter
public class TenantUpdateDTO {

    private Long id;

    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private TenantStatus status;

    private Date expireTime;

    private String remark;

    private List<Long> userIds;

}
