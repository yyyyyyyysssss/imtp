package org.imtp.api.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.imtp.api.mapper.MySqlInjector;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/1 14:51
 */
@Configuration
public class MyBatisPlusConfig {


    @Resource
    private DefaultTenantLineHandler defaultTenantLineHandler;

    @Bean
    public MySqlInjector mySqlInjector(){

        return new MySqlInjector();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(defaultTenantLineHandler);
        interceptor.addInnerInterceptor(tenantInterceptor);
        return interceptor;
    }

}
