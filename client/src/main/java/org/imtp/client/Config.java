package org.imtp.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.enums.LoadBalancerType;
import org.imtp.common.enums.ServerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    private String zooKeeperUrls;

    private LoadBalancerType loadBalancerType;

    private static volatile Config config;

    private static final Lock lock = new ReentrantLock();

    private Config (){
        File file = new File("./config.properties");
        if (file.exists()){
            try (FileInputStream fileInputStream = new FileInputStream(file)){
                Properties properties = new Properties();
                properties.load(fileInputStream);
                this.host = properties.getProperty("server.host");
                this.port = Integer.parseInt(properties.getProperty("server.port"));
                String modelProperty = properties.getProperty("server.model");
                this.model = modelProperty != null ? ServerModel.valueOf(modelProperty) : ServerModel.HOST;
                this.zooKeeperUrls = properties.getProperty("server.zookeeper.urls");
                String loadBalancerProperty = properties.getProperty("server.loadBalancer");
                this.loadBalancerType = loadBalancerProperty != null ? LoadBalancerType.valueOf(loadBalancerProperty.toUpperCase()) : LoadBalancerType.ROUND;
            }catch (IOException e){
                log.error("read config error :",e);
            }
        }else {
            ResourceBundle resourceBundle  = ResourceBundle.getBundle("config");
            this.host = resourceBundle.getString("server.host");
            this.port = Integer.parseInt(resourceBundle.getString("server.port"));
            String modelProperty = resourceBundle.getString("server.model");
            this.model = modelProperty != null ? ServerModel.valueOf(modelProperty.toUpperCase()) : ServerModel.HOST;
            this.zooKeeperUrls = resourceBundle.getString("server.zookeeper.urls");
            String loadBalancerProperty = resourceBundle.getString("server.loadBalancer");
            this.loadBalancerType = loadBalancerProperty != null ? LoadBalancerType.valueOf(loadBalancerProperty.toUpperCase()) : LoadBalancerType.ROUND;
        }
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

}
