package org.imtp.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.util.FXMLLoadUtils;

import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/12 10:44
 */
public class ChatEmoteDialog extends Dialog<String> {


    @FXML
    private TableView<String> chatEmoteTable;

    public ChatEmoteDialog(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLLoadUtils.loadUrlByFxmlPath(FXMLResourceConstant.CHAT_EMOTE_DIALOG_FML));
        fxmlLoader.setController(this);
        try {
            DialogPane dialogPane = fxmlLoader.load();
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            setDialogPane(dialogPane);
            this.initModality(Modality.NONE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
