package org.imtp.web.config.idwork;

import org.imtp.common.idwork.RandomWorkIdService;
import org.imtp.common.idwork.SnowflakeIdWorker;
import org.imtp.common.idwork.WorkIdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/28 11:10
 */
@Configuration
public class IdConfig {


    @Bean
    public WorkIdService workIdService(){

        return new RandomWorkIdService();
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(WorkIdService workIdService){

        return new SnowflakeIdWorker(workIdService);
    }

}
