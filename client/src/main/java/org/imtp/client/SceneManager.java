package org.imtp.client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.controller.Controller;
import org.imtp.client.model.MessageModel;
import org.imtp.client.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;

@Slf4j
public class SceneManager {

    private Stage stage;

    public SceneManager(Stage stage){
        this.stage = stage;
    }

    public void setScene(String fxmlPath, MessageModel messageModel) throws IOException {
        setScene(fxmlPath,null,messageModel);
    }

    public void setScene(String fxmlPath,String title,MessageModel messageModel){
        Platform.runLater(() -> {
            URL url = ResourceUtils.classPathResource(fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            Parent parent = null;
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                log.error("{}文件加载异常:", fxmlPath, e);
                throw new RuntimeException(e);
            }
            Controller controller = fxmlLoader.getController();
            controller.init(messageModel);
            controller.setSceneManager(this);
            Scene scene = new Scene(parent);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        });
    }

}
