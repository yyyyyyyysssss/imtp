package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.component.ImageUrlParse;
import org.imtp.client.entity.FriendEntity;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.enums.Gender;

import java.net.URL;

@Slf4j
public class UserFriendDetailsController extends AbstractController{

    @FXML
    private ImageView friendDetailsImg;

    @FXML
    private Label friendName;

    @FXML
    private ImageView friendGender;

    @FXML
    private Label friendNo;

    @FXML
    private ImageView friendSendMessage;

    @FXML
    private Pane friendSendMessagePane;

    private Image genderMaleImage;

    private Image genderFemaleImage;

    private Image sendMessageImage;

    @FXML
    public void initialize(){

        URL maleImageUrl = ResourceUtils.classPathResource("/img/gender_male.png");
        genderMaleImage = new Image(maleImageUrl.toExternalForm());

        URL femaleImageUrl = ResourceUtils.classPathResource("/img/gender_female.png");
        genderFemaleImage = new Image(femaleImageUrl.toExternalForm());

        URL sendMessageImageUrl = ResourceUtils.classPathResource("/img/user_friend_send_message.png");
        sendMessageImage = new Image(sendMessageImageUrl.toExternalForm());

        friendSendMessage.setImage(sendMessageImage);

        friendSendMessagePane.setOnMouseClicked(mouseEvent -> {
            log.info("clicked on friend send message");
        });
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if(object instanceof FriendEntity friendEntity){
            setData(friendEntity);
        }
    }

    @Override
    public void update(Object object) {

    }

    private void setData(FriendEntity friendEntity){
        Platform.runLater(() -> {
            friendDetailsImg.setImage(new Image(friendEntity.getAvatar()));
            friendName.setText(friendEntity.getName());
            if (friendEntity.getGender().equals(Gender.MALE)){
                friendGender.setImage(genderMaleImage);
            }else {
                friendGender.setImage(genderFemaleImage);
            }
            friendNo.setText(friendEntity.getAccount());
        });
    }

}
