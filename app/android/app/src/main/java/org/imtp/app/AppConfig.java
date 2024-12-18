package org.imtp.app;

import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppConfig {


    private static final String TAG = "AppConfig";

    private static volatile AppConfig config;

    private String apiHost;

    private AppConfig(){
        Resources resources = MainApplication.getContext().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.app);
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        }catch (IOException e) {
            Log.e(TAG,"load config error " + e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG,"load config error " + e);
            }
        }
        this.apiHost = properties.getProperty("api.host");
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
