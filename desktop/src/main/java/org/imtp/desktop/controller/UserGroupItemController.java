package org.imtp.desktop.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.entity.GroupEntity;

@Slf4j
public class UserGroupItemController extends AbstractController{

    @FXML
    private ImageView groupImg;

    @FXML
    private Label groupLabel;


    @FXML
    public void initialize(){
        Rectangle clip = new Rectangle(50, 50);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        groupImg.setClip(clip);
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if (object instanceof GroupEntity groupEntity){
            setData(groupEntity);
        }
    }

    @Override
    public void update(Object object) {

    }

    private void setData(GroupEntity groupEntity){
        Platform.runLater(() -> {
            groupImg.setImage(new Image(groupEntity.getAvatar()));
            groupLabel.setText(groupEntity.getName());
        });
    }

}
