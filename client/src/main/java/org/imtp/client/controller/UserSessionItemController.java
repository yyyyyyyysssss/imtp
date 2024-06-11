package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.entity.SessionEntity;
import org.imtp.common.enums.DeliveryMethod;

@Slf4j
public class UserSessionItemController extends AbstractController{

    @FXML
    private ImageView sessionImg;

    @FXML
    private Label nameLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label lastMsg;

    @FXML
    private Label sendUserName;

    @FXML
    private Label isAlert;

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if(object instanceof SessionEntity sessionEntity){
            setData(sessionEntity);
        }
    }

    @Override
    public void update(Object object) {

    }


    private void setData(SessionEntity sessionEntity){
        Platform.runLater(() -> {
            sessionImg.setImage(new Image(sessionEntity.getAvatar()));
            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.GROUP)
                    && sessionEntity.getLastSendMsgUserId() != null
                    && !ClientContextHolder.clientContext().id().equals(sessionEntity.getLastSendMsgUserId())){
                sendUserName.setText(sessionEntity.getLastUserName() + ":");
            }else {
                sendUserName.setText("");
            }
            nameLabel.setText(sessionEntity.getName());
            timeLabel.setText("23:34");
            lastMsg.setText(sessionEntity.getLastMsg());
            isAlert.textProperty().bind(sessionEntity.countProperty());
            sessionEntity.setCount("1");
        });
    }

}
