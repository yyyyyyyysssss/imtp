package org.imtp.api.context;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/22 14:48
 */
public class TenantContext {

    private static final InheritableThreadLocal<Long> TENANT_ID = new InheritableThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
    }

}
