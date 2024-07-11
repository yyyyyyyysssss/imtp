package org.imtp.gateway.controller;

import jakarta.annotation.Resource;
import org.imtp.gateway.config.ServiceDiscovery;
import org.imtp.gateway.config.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/11 16:04
 */
@RestController
public class ServiceDiscoveryController {

    @Resource
    private ServiceDiscovery serviceDiscovery;

    @GetMapping("/serviceDiscovery")
    public List<ServiceInfo> serviceInfos(){

        return serviceDiscovery.getServiceInfos();
    }

}
