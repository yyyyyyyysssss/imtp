package org.imtp.web.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/8 23:36
 */
@Configuration
public class ThreadPoolConfig {

    private final int corePoolSize = 10;

    private final int maxPoolSize = 200;

    private final int queueSize = 2000;

    @Bean(name = "defaultThreadPool")
    public ExecutorService defaultThreadPool(){
        ThreadPoolTaskExecutor poolExecutor=new ThreadPoolTaskExecutor();
        poolExecutor.setCorePoolSize(corePoolSize);
        poolExecutor.setMaxPoolSize(maxPoolSize);
        poolExecutor.setQueueCapacity(queueSize);
        poolExecutor.setTaskDecorator(new MDCTaskDecorator());
        poolExecutor.setThreadNamePrefix("defaultThreadPool-");
        poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        poolExecutor.initialize();
        return poolExecutor.getThreadPoolExecutor();
    }

}
