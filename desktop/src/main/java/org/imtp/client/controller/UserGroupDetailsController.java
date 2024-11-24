package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.Callback;
import org.imtp.client.entity.GroupEntity;
import org.imtp.client.util.ResourceUtils;

import java.net.URL;

@Slf4j
public class UserGroupDetailsController extends AbstractController{

    @FXML
    private HBox userGroupDetailsHbox;

    @FXML
    private ImageView groupDetailsImg;

    @FXML
    private Text groupName;

    @FXML
    private ImageView groupSendMessage;

    @FXML
    private VBox groupSendMessagePane;

    private GroupEntity groupEntity;

    private Image sendMessageImage;

    private Callback<Long> callback;

    @FXML
    public void initialize(){

        URL sendMessageImageUrl = ResourceUtils.classPathResource("/img/user_group_send_message.png");
        sendMessageImage = new Image(sendMessageImageUrl.toExternalForm());

        groupSendMessage.setImage(sendMessageImage);

        UserGroupDetailsController that = this;

        groupSendMessagePane.setOnMouseClicked(mouseEvent -> {
            callback.callback(groupEntity.getId());
        });

        Rectangle clip = new Rectangle(100, 100);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        groupDetailsImg.setClip(clip);

    }


    public void setCallback(Callback<Long> callback) {
        this.callback = callback;
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if(object instanceof GroupEntity groupEntity){
            this.groupEntity = groupEntity;
            setData(groupEntity);
        }
    }

    @Override
    public void update(Object object) {

    }

    private void setData(GroupEntity groupEntity){
        Platform.runLater(() -> {
            groupDetailsImg.setImage(new Image(groupEntity.getAvatar()));
            groupName.setText(groupEntity.getName());
        });
    }

}
