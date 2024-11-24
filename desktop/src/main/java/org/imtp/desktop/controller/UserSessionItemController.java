package org.imtp.desktop.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.entity.SessionEntity;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;

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
        alertCircle.setVisible(false);

        Rectangle clip = new Rectangle(50, 50);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        sessionImg.setClip(clip);

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
            nameLabel.setText(sessionEntity.getName());
            StringBuilder sb = new StringBuilder();
            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.GROUP)
                    && sessionEntity.getLastSendMsgUserId() != null
                    && !ClientContextHolder.clientContext().id().equals(sessionEntity.getLastSendMsgUserId())){
                sb.append(sessionEntity.getLastUserName()).append("：");
            }
            if (MessageType.TEXT_MESSAGE.equals(sessionEntity.getLastMsgType())){
                sb.append(sessionEntity.getLastMsg());
            }else if (MessageType.IMAGE_MESSAGE.equals(sessionEntity.getLastMsgType())){
                sb.append("[图片]");
            }else if (MessageType.VIDEO_MESSAGE.equals(sessionEntity.getLastMsgType())){
                sb.append("[视频]");
            }else if (MessageType.VOICE_MESSAGE.equals(sessionEntity.getLastMsgType())){
                sb.append("[语音]");
            }else if (MessageType.FILE_MESSAGE.equals(sessionEntity.getLastMsgType())){
                sb.append("[文件]");
            }
            if (!sb.toString().isEmpty()){
                lastMsg.setText(sb.toString());
            }else {
                lastMsg.setText(null);
            }
            //时间处理
            timeLabel.setText("23:34");
            //消息计数
            messageCount.textProperty().bind(sessionEntity.countProperty());
            if (sessionEntity.getCount() == null || sessionEntity.getCount().isEmpty()){
                alertCircle.setVisible(false);
            }else {
                alertCircle.setVisible(true);
            }
        });
    }

}
