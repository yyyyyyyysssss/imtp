package org.imtp.app;

import android.util.Log;

import java.io.File;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppConfig {


    private static final String TAG = "AppConfig";

    private static volatile AppConfig config;

    private String apiHost;

    private AppConfig(){
        File file = new File("./app.properties");
        ResourceBundle resourceBundle  = ResourceBundle.getBundle("app");
        if (resourceBundle == null){
            Log.e(TAG,"配置文件读取失败");
            throw new RuntimeException();
        }
        this.apiHost = resourceBundle.getString("api.host");
        Log.i(TAG,"apiHost: " + apiHost);
    }

    private static final Lock lock = new ReentrantLock();



    public static AppConfig getInstance(){
        if (config == null){
            try {
                lock.lock();
                if (config == null){
                    config = new AppConfig();
                }
            }finally {
                lock.unlock();
            }
        }
        return config;
    }

    public String getApiHost() {
        return apiHost;
    }
}
