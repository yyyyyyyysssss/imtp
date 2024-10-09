package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.entity.FriendEntity;

@Slf4j
public class UserFriendItemController extends AbstractController{

    @FXML
    private ImageView friendImg;

    @FXML
    private Label friendLabel;

    @FXML
    public void initialize(){
        Rectangle clip = new Rectangle(50, 50);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        friendImg.setClip(clip);
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if (object instanceof FriendEntity friendEntity){
            setData(friendEntity);
        }
    }

    @Override
    public void update(Object object) {

    }

    private void setData(FriendEntity friendEntity){
        Platform.runLater(() -> {
            friendImg.setImage(new Image(friendEntity.getAvatar()));
            friendLabel.setText(friendEntity.getName());
        });
    }

}
