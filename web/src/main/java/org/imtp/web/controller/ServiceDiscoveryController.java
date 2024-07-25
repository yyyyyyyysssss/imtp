package org.imtp.web.controller;

import jakarta.annotation.Resource;
import org.imtp.web.config.zookeeper.ServiceDiscovery;
import org.imtp.web.config.zookeeper.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/11 16:04
 */
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
