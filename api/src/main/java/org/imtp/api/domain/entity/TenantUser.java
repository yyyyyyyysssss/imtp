package org.imtp.api.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 17:28
 */
@Getter
@Setter
@TableName("tenant_user")
@Builder
public class TenantUser {

    @Tolerate
    public TenantUser(){
    }

    private Long id;

    private Long tenantId;

    private Long userId;


}
