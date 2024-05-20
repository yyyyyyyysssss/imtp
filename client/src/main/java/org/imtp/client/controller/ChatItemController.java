package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.imtp.client.entity.ChatItemEntity;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/20 16:34
 */
public class ChatItemController extends AbstractController{

    @FXML
    private HBox chatItemHBox;

    @FXML
    private ImageView chatItemImageView;

    @FXML
    private Label chatItemLabel;

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if (object instanceof ChatItemEntity chatItemEntity){
            Platform.runLater(() -> {
                chatItemImageView.setImage(new Image(chatItemEntity.getAvatar()));
                chatItemLabel.setText(chatItemEntity.getContent());
                double maxWidth = chatItemLabel.getMaxWidth();
                chatItemLabel.widthProperty().addListener((observableValue, oldVal, newVal) -> {
                    if (newVal.doubleValue() <= maxWidth){

                    }
                });
                chatItemHBox.setAlignment(Pos.CENTER_LEFT);
            });
        }
    }

    @Override
    public void update(Object object) {

    }
}
