package org.imtp.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.enums.LoadBalancerType;
import org.imtp.common.enums.ServerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/28 23:08
 */
@Slf4j
@Getter
public class Config {

    private String host;

    private Integer port;

    private ServerModel model;

    private LoadBalancerType loadBalancerType;

    private String apiHost;

    private String serviceDiscoveryUrl;

    private static volatile Config config;

    private static final Lock lock = new ReentrantLock();

    private Config (){
        File file = new File("./config.properties");
        ResourceBundle resourceBundle = null;
        if (file.exists()){
            log.info("正在加载当前目录配置文件");
            try {
                resourceBundle = ResourceBundle.getBundle("config", Locale.getDefault(), new URLClassLoader(new URL[] { file.toURI().toURL() }));
            }catch (IOException e){
                log.error("当前目录配置文件读取失败:",e);
            }
        }else {
            log.info("正在加载类路径下配置文件");
            resourceBundle  = ResourceBundle.getBundle("config");
        }
        if (resourceBundle == null){
            log.error("配置文件读取失败");
            throw new RuntimeException();
        }
        this.host = resourceBundle.getString("server.host");
        this.port = Integer.parseInt(resourceBundle.getString("server.port"));
        String modelProperty = resourceBundle.getString("server.model");
        this.model = ServerModel.valueOf(modelProperty.toUpperCase());
        String loadBalancerProperty = resourceBundle.getString("server.loadBalancer");
        this.loadBalancerType = LoadBalancerType.valueOf(loadBalancerProperty.toUpperCase());
        this.apiHost = resourceBundle.getString("api.host");
        this.serviceDiscoveryUrl = resourceBundle.getString("api.serviceDiscovery.url");
        log.info("配置文件加载完成：{}",this);
    }

    public static Config getInstance(){
        if (config == null){
            try {
                lock.lock();
                if (config == null){
                    config = new Config();
                }
            }finally {
                lock.unlock();
            }
        }
        return config;
    }

    @Override
    public String toString() {
        return "Config{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", model=" + model +
                ", loadBalancerType=" + loadBalancerType +
                '}';
    }
}
