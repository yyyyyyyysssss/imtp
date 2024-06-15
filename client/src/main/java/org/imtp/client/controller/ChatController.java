package org.imtp.client.controller;


import io.netty.channel.EventLoop;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.component.ImageUrlParse;
import org.imtp.client.context.ClientContext;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.event.UserSessionEvent;
import org.imtp.client.idwork.IdGen;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.MessageStateResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;
import java.net.URL;
import java.util.HashMap;
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
    private TextArea inputText;

    @FXML
    private Button sendButton;

    private SessionEntity sessionEntity;

    private Image sendingImage;

    private Image sendFailureImage;

    private Map<Long,ChatItemEntity> ackChatItemEntityMap;

    private UserFriendController userFriendController;

    private UserGroupController userGroupController;

    private ChatEmoteDialog dialog;

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

        sendButton.setOnMouseClicked(mouseEvent -> {
            sendTextMessage();
        });
        chatListView.setCellFactory(c -> new ChatItemListCell());
        chatListView.setFocusTraversable(false);

        chatEmoteIcon.setOnMouseClicked(mouseEvent -> {
            if (dialog == null){
                dialog = new ChatEmoteDialog();
                dialog.setX(500);
                dialog.setY(100);
                dialog.show();
            }else {
                if (dialog.isShowing()){
                    dialog.setX(Double.NaN);
                    dialog.setY(Double.NaN);
                    dialog.close();
                }else {
                    dialog.setX(500);
                    dialog.setY(100);
                    dialog.show();
                }
            }
        });

    }

    @Override
    protected void init0() {

    }

    public void setUserFriendController(UserFriendController userFriendController) {
        this.userFriendController = userFriendController;
    }

    public void setUserGroupController(UserGroupController userGroupController) {
        this.userGroupController = userGroupController;
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
        Long ackId = IdGen.genId();
        Packet packet;
        if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),ackId);
        }else {
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),ackId,true);
        }
        ChatItemEntity selfChatItemEntity = ChatItemEntity.createSelfChatItemEntity();
        selfChatItemEntity.setContent(text);
        selfChatItemEntity.setMessageType(MessageType.findMessageTypeByValue((int) packet.getCommand().getCmdCode()));
        addChatItem(selfChatItemEntity);
        selfChatItemEntity.setImage(sendingImage);

        ackChatItemEntityMap.put(ackId,selfChatItemEntity);

        sessionEntity.setLastSendMsgUserId(ClientContextHolder.clientContext().id());
        sessionEntity.setLastMsgType(MessageType.TEXT_MESSAGE);
        sessionEntity.setLastMsg(text);
        chatVbox.fireEvent(new UserSessionEvent(UserSessionEvent.SEND_MESSAGE,sessionEntity));
        send(packet);

        inputText.clear();

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

}
