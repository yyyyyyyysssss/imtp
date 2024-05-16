package org.imtp.client.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.controller.Controller;

import java.io.IOException;
import java.net.URL;

@Slf4j
public class FXMLLoadUtils {

    public static Tuple2<Node, Controller> loadFxmlAndControl(String fxmlPath) {
        URL url = ResourceUtils.classPathResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(url);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Node node = null;
        try {
            node = fxmlLoader.load();
        } catch (IOException e) {
            log.error("{}文件加载异常:", fxmlPath, e);
        }
        Controller controller = fxmlLoader.getController();
        return new Tuple2<>(node,controller);
    }

    public static Node loadFxml(String fxmlPath) {
        try {
            URL url = ResourceUtils.classPathResource(fxmlPath);
            return FXMLLoader.load(url);
        } catch (IOException e) {
            log.error("{}文件加载异常:", fxmlPath, e);
        }
        return null;
    }

}
