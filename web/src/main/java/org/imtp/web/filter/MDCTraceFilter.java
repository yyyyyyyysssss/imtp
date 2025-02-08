package org.imtp.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.imtp.web.config.constant.CommonConstant;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/8 23:27
 */
@Order(-1)
@Component
public class MDCTraceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId;
        if ((traceId=request.getHeader(CommonConstant.TRACE_ID)) == null){
            traceId=UUID.randomUUID().toString().replaceAll("-","");
        }
        try {
            MDC.put(CommonConstant.TRACE_ID,traceId);
            filterChain.doFilter(request,response);
        }finally {
            MDC.clear();
        }

    }
}
