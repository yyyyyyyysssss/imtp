package org.imtp.desktop;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.imtp.desktop.constant.FXMLResourceConstant;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.handler.AuthenticationHandler;
import org.imtp.desktop.util.ResourceUtils;

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
        sceneManager.setScene(FXMLResourceConstant.LOGIN_FXML,"登录页",new AuthenticationHandler(),false);
        URL url = ResourceUtils.classPathResource("/img/taskbar_icon.png");
        stage.getIcons().add(new Image(url.toExternalForm()));
    }

    @Override
    public void stop() {
        ClientContextHolder.clientContext().channel().close();
        System.exit(0);
    }
}
