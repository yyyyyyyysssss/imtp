package org.imtp.client;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        stage.initStyle(StageStyle.UNDECORATED);
        SceneManager sceneManager = SceneManagerHolder.createSceneManager(stage);
        sceneManager.setScene(FXMLResourceConstant.LOGIN_FXML,"登录页",new LoginHandler(),false);
        URL url = ResourceUtils.classPathResource("/img/taskbar_icon.png");
        stage.getIcons().add(new Image(url.toExternalForm()));
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
