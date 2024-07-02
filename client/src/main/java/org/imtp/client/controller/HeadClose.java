package org.imtp.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.imtp.client.SceneManager;
import org.imtp.client.SceneManagerHolder;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/2 17:41
 */
public class HeadClose {

    @FXML
    private VBox headMinimizeVBxo;

    @FXML
    private VBox headMaximizeVBxo;

    @FXML
    private VBox headCloseVBxo;


    @FXML
    public void initialize(){
        SceneManager sceneManager = SceneManagerHolder.getSceneManager();
        Stage stage = sceneManager.getStage();
        headMinimizeVBxo.setOnMouseClicked(event -> {
            stage.setIconified(true);
        });
        headMaximizeVBxo.setOnMouseClicked(event -> {
            if (stage.isMaximized()){
                stage.setMaximized(false);
            }else {
                stage.setMaximized(true);
            }

        });
        headCloseVBxo.setOnMouseClicked(event -> {
            stage.close();
        });
    }
}
