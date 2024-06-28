package org.imtp.client;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.handler.LoginHandler;
import org.imtp.client.util.ResourceUtils;

import java.net.URL;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 9:43
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.setScene(FXMLResourceConstant.LOGIN_FXML,"登录页",new LoginHandler());
        URL url = ResourceUtils.classPathResource("/img/taskbar_icon.png");
        stage.getIcons().add(new Image(url.toExternalForm()));
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
