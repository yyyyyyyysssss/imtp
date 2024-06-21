package org.imtp.client.controller;


import io.netty.channel.EventLoop;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.AutoResizeTextFiled;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.event.EmojiEvent;
import org.imtp.client.event.UserSessionEvent;
import org.imtp.client.idwork.IdGen;
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.TextUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatController extends AbstractController{

    @FXML
    private VBox chatVbox;

    @FXML
    private ListView<ChatItemEntity> chatListView;

    @FXML
    private ImageView chatEmoteIcon;

    @FXML
    private ImageView chatFileIcon;

    @FXML
    private TextFlow inputTextFlow;

    @FXML
    private TextField testText;

    @FXML
    private Button sendButton;

    private SessionEntity sessionEntity;

    private Image sendingImage;

    private Image sendFailureImage;

    private Map<Long,ChatItemEntity> ackChatItemEntityMap;

    private UserFriendController userFriendController;

    private UserGroupController userGroupController;

    private ChatEmojiDialog dialog;

    private Map<Long,RetryTask> retryTaskMap;


    @FXML
    public void initialize(){
        URL sendFailImageUrl = ResourceUtils.classPathResource("/img/send_fail.png");
        sendFailureImage = new Image(sendFailImageUrl.toExternalForm());

        URL sendingImageUrl = ResourceUtils.classPathResource("/img/sending.gif");
        sendingImage = new Image(sendingImageUrl.toExternalForm());

        URL chatEmoteIconUrl = ResourceUtils.classPathResource("/img/emote_icon.png");
        chatEmoteIcon.setImage(new Image(chatEmoteIconUrl.toExternalForm()));

        URL chatFileIconUrl = ResourceUtils.classPathResource("/img/file_icon.png");
        chatFileIcon.setImage(new Image(chatFileIconUrl.toExternalForm()));

        ackChatItemEntityMap = new ConcurrentHashMap<>();
        retryTaskMap = new ConcurrentHashMap<>();

        TextInputControl textInput = createTextArea();
        inputTextFlow.getChildren().add(textInput);

        sendButton.setOnMouseClicked(mouseEvent -> {
            sendMessage();
        });
        chatListView.setCellFactory(c -> new ChatItemListCell());
        chatListView.setFocusTraversable(false);

        chatEmoteIcon.setOnMouseClicked(mouseEvent -> {
            if (dialog == null){
                dialog = new ChatEmojiDialog();
                dialog.getDialogPane().addEventHandler(EmojiEvent.SELECTED, emojiEvent -> {
                    ObservableList<Node> children = inputTextFlow.getChildren();
                    TextInputControl inputText = (TextInputControl)children.getLast();
                    int caretPosition = inputText.getCaretPosition();
                    inputText.insertText(caretPosition,emojiEvent.getEmojiValue());
                    dialog.close();
                });
                Window window = chatVbox.getScene().getWindow();
                dialog.initOwner(window);
                chatVbox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        dialog.close();
                    }
                });
            }
            dialog.showEmojiPane(chatEmoteIcon);
        });
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

    private TextField createTextField(){
        TextField textField = new AutoResizeTextFiled();
        textField.setPrefColumnCount(1);
        return textField;
    }

    private TextArea createTextArea(){
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setMaxHeight(inputTextFlow.getPrefHeight());
        textArea.setMaxWidth(inputTextFlow.getPrefWidth());
        textArea.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case ENTER :
                    int caretPosition = textArea.getCaretPosition();
                    keyEvent.consume();
                    if(keyEvent.isShiftDown()){
                        textArea.appendText(System.lineSeparator());
                    }else {
                        //去除末尾的换行符
                        String text = textArea.getText();
                        text =  text.substring(0,caretPosition-1);
                        textArea.setText(text);
                        sendMessage();
                    }
                    break;
            }
        });
        return textArea;
    }

    private void sendMessage(){
        ObservableList<Node> children = inputTextFlow.getChildren();
        TextInputControl inputText = (TextInputControl)children.getFirst();
        String text = inputText.getText();
        if (text.isEmpty()){
            return;
        }
        inputText.clear();
        sendMessage(text,MessageType.TEXT_MESSAGE);
    }

    private void sendMessage(Object object,MessageType messageType){
        sessionEntity.setLastSendMsgUserId(ClientContextHolder.clientContext().id());
        ChatItemEntity selfChatItemEntity = ChatItemEntity.createSelfChatItemEntity();
        Long ackId = IdGen.genId();
        Packet packet = null;
        switch (messageType){
            case TEXT_MESSAGE :
                String message = (String) object;
                if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
                    packet = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),ackId);
                }else {
                    packet = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),ackId,true);
                }
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(message);
                selfChatItemEntity.setContent(message);
                selfChatItemEntity.setMessageType(messageType);
                break;
        }
        addChatItem(selfChatItemEntity);
        selfChatItemEntity.setImage(sendingImage);
        ackChatItemEntityMap.put(ackId,selfChatItemEntity);
        //联动会话框
        chatVbox.fireEvent(new UserSessionEvent(UserSessionEvent.SEND_MESSAGE,sessionEntity));
        //发送消息
        send(packet);
        //异步等待消息回执
        EventLoop eventLoop = ClientContextHolder.clientContext().channel().eventLoop();
        RetryTask retryTask = new RetryTask();
        retryTask.setScheduledFuture(eventLoop.schedule(() -> {
            ChatItemEntity chatItemEntity = ackChatItemEntityMap.get(ackId);
            if (chatItemEntity != null){
                chatItemEntity.setImage(sendFailureImage);
            }
            retryTaskMap.remove(ackId);
        },10, TimeUnit.SECONDS));
        retryTaskMap.put(ackId,retryTask);
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        if (packet.getSender() != 0 && !packet.realSender().equals(sessionEntity.getReceiverUserId())){
            return;
        }
        switch (packet.getHeader().getCmd()){
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) packet;
                ChatItemEntity chatItemEntity = new ChatItemEntity();
                chatItemEntity.setSelf(false);
                if (textMessage.isGroup()){
                    UserFriendInfo groupUserInfo = userGroupController.findGroupUserInfo(textMessage.getReceiver(), textMessage.getSender());
                    String imageUrl = loadImageUrl(groupUserInfo.getAvatar());
                    chatItemEntity.setAvatar(imageUrl);
                    chatItemEntity.setName(groupUserInfo.getNickname());
                }else {
                    chatItemEntity.setAvatar(sessionEntity.getAvatar());
                    chatItemEntity.setName(sessionEntity.getName());
                }
                chatItemEntity.setMessageType(MessageType.findMessageTypeByValue((int) textMessage.getCommand().getCmdCode()));
                chatItemEntity.setContent(textMessage.getMessage());
                chatItemEntity.setDeliveryMethod(textMessage.isGroup() ? DeliveryMethod.GROUP : DeliveryMethod.SINGLE);
                addChatItem(chatItemEntity);
                break;
            case MSG_RES:
                MessageStateResponse messageStateResponse = (MessageStateResponse) packet;
                ChatItemEntity cie = ackChatItemEntityMap.get(messageStateResponse.getAckId());
                if (cie != null){
                    switch (messageStateResponse.getState()){
                        case DELIVERED :
                            log.info("{},{}",messageStateResponse.getState(),messageStateResponse.getAckId());
                            cie.imageProperty().set(null);
                            ackChatItemEntityMap.remove(messageStateResponse.getAckId());
                            RetryTask retryTask = retryTaskMap.get(messageStateResponse.getAckId());
                            if (retryTask != null){
                                retryTask.cancel();
                                retryTaskMap.remove(messageStateResponse.getAckId());
                            }
                            break;
                    }
                }
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
        String imageUrl = loadImageUrl(sessionEntity.getLastUserAvatar());
        chatItemEntity.setAvatar(imageUrl);
        chatItemEntity.setName(sessionEntity.getLastUserName());
        if (ClientContextHolder.clientContext().id().equals(sessionEntity.getLastSendMsgUserId())){
            chatItemEntity.setSelf(true);
        }else {
            chatItemEntity.setSelf(false);
        }
        chatItemEntity.setMessageType(sessionEntity.getLastMsgType());
        chatItemEntity.setContent(sessionEntity.getLastMsg());
        chatItemEntity.setDeliveryMethod(sessionEntity.getDeliveryMethod());
        addChatItem(chatItemEntity);
    }

    private void addChatItem(ChatItemEntity chatItemEntity){
        Platform.runLater(() -> {
            ObservableList<ChatItemEntity> items = chatListView.getItems();
            int index = items.size();
            items.addLast(chatItemEntity);
            chatListView.scrollTo(index);
        });
    }


    public void setUserFriendController(UserFriendController userFriendController) {
        this.userFriendController = userFriendController;
    }

    public void setUserGroupController(UserGroupController userGroupController) {
        this.userGroupController = userGroupController;
    }

}
