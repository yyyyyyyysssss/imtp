package org.imtp.api.context;

import org.springframework.core.task.TaskDecorator;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/22 15:14
 */
public class TenantTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Long tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            }finally {
                TenantContext.clear();
            }
        };
    }
}
