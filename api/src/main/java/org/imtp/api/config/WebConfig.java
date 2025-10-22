package org.imtp.api.config;

import org.imtp.api.config.jackson.SensitiveContextInterceptor;
import org.imtp.api.interceptor.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/8 16:53
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET","POST", "PUT", "DELETE", "PATCH");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 字段脱敏
        registry.addInterceptor(new SensitiveContextInterceptor())
                .addPathPatterns("/**");
        // 多租户拦截器
        registry.addInterceptor(new TenantInterceptor())
                .addPathPatterns("/**");
    }
}
