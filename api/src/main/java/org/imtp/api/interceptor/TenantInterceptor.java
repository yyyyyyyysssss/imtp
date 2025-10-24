package org.imtp.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.config.constant.CommonConstant;
import org.imtp.api.context.TenantContext;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/22 14:51
 */
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(CommonConstant.TENANT_ID);
        if (tenantId != null && !tenantId.isEmpty()) {
            Long tenantIdLong = Long.parseLong(tenantId);
            TenantContext.setTenantId(tenantIdLong);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
