package org.imtp.web.config.mybatis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.imtp.web.mapper.MySqlInjector;

/**
 * @Description
 * @Author ys
 * @Date 2023/7/1 14:51
 */
@Configuration
public class MyBatisPlusConfig {


    @Bean
    public MySqlInjector mySqlInjector(){

        return new MySqlInjector();
    }

}
