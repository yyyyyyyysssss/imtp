package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.api.config.jackson.Sensitive;
import org.imtp.api.config.jackson.SensitiveType;
import org.imtp.api.enums.TenantStatus;

import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:32
 */
@Getter
@Setter
public class TenantVO {

    private Long id;

    private String tenantCode;

    private String tenantName;

    private String logo;

    private String contactName;

    @Sensitive(SensitiveType.MOBILE)
    private String contactPhone;

    @Sensitive(SensitiveType.EMAIL)
    private String contactEmail;

    private TenantStatus status;

    private String expireTime;

    private String remark;

    protected String createTime;

    protected String updateTime;

    private List<Long> userIds;

}
