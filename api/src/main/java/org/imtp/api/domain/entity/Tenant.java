package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Tolerate;
import org.imtp.api.enums.TenantStatus;

import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:13
 */
@Getter
@Setter
@TableName("tenant")
@Builder
public class Tenant extends BaseEntity{

    @Tolerate
    public Tenant(){
    }

    private Long id;

    @TableField("tenant_code")
    private String tenantCode;

    @TableField("tenant_name")
    private String tenantName;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_email")
    private String contactEmail;

    @TableField("status")
    private TenantStatus status;

    @TableField("expire_time")
    private Date expireTime;

    @TableField("remark")
    private String remark;

    @TableField("create_by")
    private Long createBy;

    @TableField("update_by")
    private Long updateBy;

}
