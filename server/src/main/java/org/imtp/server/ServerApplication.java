package org.imtp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/10 11:44
 */
@SpringBootApplication
@EnableAsync
@EnableFeignClients
public class ServerApplication{

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);
    }
}
