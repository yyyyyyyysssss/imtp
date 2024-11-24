package org.imtp.desktop;

import javafx.stage.Stage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/2 17:51
 */
public class SceneManagerHolder {

    private static volatile SceneManager sceneManager;

    private static final Lock lock = new ReentrantLock();

    public static SceneManager createSceneManager(Stage stage){
        if (sceneManager == null){
            try {
                lock.lock();
                if (sceneManager == null){
                    sceneManager = new SceneManager(stage);
                }
            }finally {
                lock.unlock();
            }
        }
        return sceneManager;
    }

    public static SceneManager getSceneManager(){
        if (sceneManager == null){
            throw new NullPointerException("sceneManager is null");
        }
        return sceneManager;
    }

}
