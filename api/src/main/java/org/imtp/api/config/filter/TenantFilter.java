package org.imtp.api.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.api.context.TenantContext;
import org.imtp.common.config.constant.CommonConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            Long tenantId = getTenantId(request);
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
            }
            filterChain.doFilter(request, response);
        }finally {
            TenantContext.clear();
        }
    }

    private Long getTenantId(HttpServletRequest request){
        String tenantId = request.getHeader(CommonConstant.TENANT_ID);
        if(tenantId == null || tenantId.isEmpty()){
            tenantId = request.getParameter(CommonConstant.TENANT_ID);
        }
        if(tenantId != null && !tenantId.isEmpty()){
            return Long.parseLong(tenantId);
        }
        return null;
    }

}
