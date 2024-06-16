package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
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
    private StackPane itemStackPane;

    @FXML
    private Text messageCount;

    @FXML
    private Circle alertCircle;

    private static final int R = 10;


    @FXML
    public void initialize(){
        alertCircle.setRadius(R);
        alertCircle.setStroke(Color.RED);
        alertCircle.setStrokeWidth(10);
        alertCircle.setStrokeType(StrokeType.INSIDE);
        alertCircle.setFill(Color.RED);
        alertCircle.relocate(0, 0);

        messageCount.setBoundsType(TextBoundsType.VISUAL);
        double W = messageCount.getBoundsInLocal().getWidth();
        double H = messageCount.getBoundsInLocal().getHeight();
        messageCount.relocate(R - W / 2, R - H / 2);
        messageCount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1 != null && !t1.isEmpty()){
                    alertCircle.setVisible(true);
                }else {
                    alertCircle.setVisible(false);
                }
            }
        });
    }

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
            messageCount.textProperty().bind(sessionEntity.countProperty());
            if (sessionEntity.getCount() == null || sessionEntity.getCount().isEmpty()){
                alertCircle.setVisible(false);
            }
        });
    }

}
