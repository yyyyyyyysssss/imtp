package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.imtp.client.entity.ChatItemEntity;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/20 16:34
 */
public class ChatItemController extends AbstractController{

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
            });

        }
    }

    @Override
    public void update(Object object) {

    }
}
