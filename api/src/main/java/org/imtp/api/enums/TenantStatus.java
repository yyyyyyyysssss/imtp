package org.imtp.api.enums;

import lombok.Getter;

@Getter
public enum TenantStatus {

    // 待启用
    PENDING,

    // 使用中
    ACTIVE,

    // 已停用
    DISABLED,

    // 已过期
    EXPIRED,

}
