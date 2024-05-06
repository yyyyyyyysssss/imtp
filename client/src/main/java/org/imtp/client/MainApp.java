package org.imtp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle("登录页");
        stage.setScene(scene);
        stage.show();
    }

}
