package org.imtp.api.controller;

import jakarta.annotation.Resource;
import org.imtp.api.config.zookeeper.ServiceDiscovery;
import org.imtp.api.config.zookeeper.ServiceInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/11 16:04
 */
@ConditionalOnProperty(name = "zookeeper.enabled", havingValue = "true")
@RestController
@RequestMapping("/service")
public class ServiceDiscoveryController {

    @Resource
    private ServiceDiscovery serviceDiscovery;

    @GetMapping("/discovery")
    public List<ServiceInfo> discovery(){

        return serviceDiscovery.getServiceInfos();
    }

    @GetMapping("/remove")
    public boolean remove(){

        return true;
    }

}
