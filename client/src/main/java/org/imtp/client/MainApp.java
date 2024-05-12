package org.imtp.client;

import javafx.stage.Stage;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.handler.LoginHandler;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 9:43
 */
public class MainApp extends AbstractApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.setScene(FXMLResourceConstant.LOGIN_FXML,"登录页",new LoginHandler());
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
