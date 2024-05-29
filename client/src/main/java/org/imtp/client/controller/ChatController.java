package org.imtp.client.controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.event.UserSessionEvent;
import org.imtp.client.idwork.IdGen;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;

import java.net.URL;
import java.util.List;

@Slf4j
public class ChatController extends AbstractController{

    @FXML
    private VBox chatVbox;

    @FXML
    private ListView<ChatItemEntity> chatListView;

    @FXML
    private TextArea inputText;

    @FXML
    private Button sendButton;

    private SessionEntity sessionEntity;

    private Image sendFailureImage;

    @FXML
    public void initialize(){
        URL sendFailImageUrl = ResourceUtils.classPathResource("/img/send_fail.png");
        sendFailureImage = new Image(sendFailImageUrl.toExternalForm());

        inputText.setWrapText(true);
        inputText.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case ENTER :
                    int caretPosition = inputText.getCaretPosition();
                    keyEvent.consume();
                    if(keyEvent.isShiftDown()){
                        inputText.appendText(System.lineSeparator());
                    }else {
                        //去除末尾的换行符
                        String text = inputText.getText();
                        text =  text.substring(0,caretPosition-1);
                        inputText.setText(text);
                        sendTextMessage();
                    }
                    break;
            }
        });

        sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                sendTextMessage();
            }
        });
        chatListView.setCellFactory(c -> new ChatItemListCell());
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        this.sessionEntity = (SessionEntity) object;
        if (sessionEntity != null && sessionEntity.getLastMsg() != null){
            addChatItem(sessionEntity);
        }
    }

    private void sendTextMessage(){
        String text = inputText.getText();
        if (text.isEmpty()){
            inputText.clear();
            return;
        }
        Packet packet;
        if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId());
        }else {
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),true);
        }
        ChatItemEntity selfChatItemEntity = ChatItemEntity.createSelfChatItemEntity();
        selfChatItemEntity.setContent(text);
        selfChatItemEntity.setMessageType(MessageType.findMessageTypeByValue((int) packet.getCommand().getCmdCode()));
        addChatItem(selfChatItemEntity);

        sessionEntity.setLastMsgType(MessageType.TEXT_MESSAGE);
        sessionEntity.setLastMsg(text);
        chatVbox.fireEvent(new UserSessionEvent(UserSessionEvent.SEND_MESSAGE,sessionEntity));
        send(packet);

//        Timeline timeline = new Timeline();
//        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
//            selfChatItemEntity.imageProperty().set(null);
//        });
//        timeline.getKeyFrames().add(keyFrame);
//        timeline.setCycleCount(1);
//        timeline.play();

        inputText.clear();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        if (!packet.getSender().equals(sessionEntity.getReceiverUserId())){
            return;
        }
        switch (packet.getHeader().getCmd()){
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) packet;
                ChatItemEntity chatItemEntity = new ChatItemEntity();
                chatItemEntity.setSelf(false);
                chatItemEntity.setAvatar(sessionEntity.getAvatar());
                chatItemEntity.setMessageType(MessageType.findMessageTypeByValue((int) textMessage.getCommand().getCmdCode()));
                chatItemEntity.setContent(textMessage.getMessage());
                addChatItem(chatItemEntity);
                break;
        }
    }

    private void setListView(List<ChatItemEntity> chatItemEntities){
        ObservableList<ChatItemEntity> chatItemEntityObservableList = FXCollections.observableArrayList(chatItemEntities);
        chatListView.setItems(chatItemEntityObservableList);
    }

    private void addChatItem(SessionEntity sessionEntity){
        ChatItemEntity chatItemEntity = new ChatItemEntity();
        chatItemEntity.setId(IdGen.genId());
        chatItemEntity.setSelf(false);
        chatItemEntity.setAvatar(sessionEntity.getAvatar());
        chatItemEntity.setMessageType(sessionEntity.getLastMsgType());
        chatItemEntity.setContent(sessionEntity.getLastMsg());
        addChatItem(chatItemEntity);
    }

    private void addChatItem(ChatItemEntity chatItemEntity){
        Platform.runLater(() -> {
            ObservableList<ChatItemEntity> items = chatListView.getItems();
            items.addLast(chatItemEntity);
            int size = items.size();
            chatListView.scrollTo(size - 1);
        });

    }

}
