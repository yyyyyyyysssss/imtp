package org.imtp.client.controller;


import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.util.EmojiImageUtils;
import com.gluonhq.richtextarea.RichTextArea;
import io.netty.channel.EventLoop;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ChunkedUploader;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.event.EmojiEvent;
import org.imtp.client.event.UserSessionEvent;
import org.imtp.client.idwork.IdGen;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatController extends AbstractController {

    @FXML
    private VBox chatVbox;

    @FXML
    private ListView<ChatItemEntity> chatListView;

    @FXML
    private HBox chatEmojiHBox;

    @FXML
    private ImageView chatEmoteIcon;

    @FXML
    private ImageView chatFileIcon;

    @FXML
    private RichTextArea richTextArea;

    @FXML
    private HBox chatSendHBox;

    @FXML
    private Button sendButton;

    private SessionEntity sessionEntity;

    private Image sendingImage;

    private Image sendFailureImage;

    private Map<Long, ChatItemEntity> ackChatItemEntityMap;

    private UserFriendController userFriendController;

    private UserGroupController userGroupController;

    private ChatEmojiDialog dialog;

    private Map<Long, RetryTask> retryTaskMap;


    @FXML
    public void initialize() {
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

        richTextArea.setAutoSave(true);
        richTextArea.setOnAction(actionEvent -> {
            sendMessage();
        });

        sendButton.setOnMouseClicked(mouseEvent -> {
            sendMessage();
        });
        chatListView.setCellFactory(c -> new ChatItemListCell());
        chatListView.setFocusTraversable(false);
        chatEmoteIcon.setOnMouseClicked(mouseEvent -> {
            if (dialog == null) {
                dialog = new ChatEmojiDialog();
                dialog.getDialogPane().addEventHandler(EmojiEvent.SELECTED, emojiEvent -> {
                    Emoji emoji = emojiEvent.getEmoji();
                    richTextArea.getActionFactory().insertEmoji(emoji).execute(new ActionEvent());
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

        chatVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                chatListView.setPrefHeight(t1.doubleValue() - 250);
            }
        });

        chatListView.prefWidthProperty().bind(chatVbox.widthProperty());
        chatEmojiHBox.prefWidthProperty().bind(chatVbox.widthProperty());
        richTextArea.prefWidthProperty().bind(chatVbox.widthProperty());
        chatSendHBox.prefWidthProperty().bind(chatVbox.widthProperty());
    }

    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        this.sessionEntity = (SessionEntity) object;
        if (sessionEntity != null && sessionEntity.getLastMsg() != null) {
            addChatItem(sessionEntity);
        }
    }

    private void sendMessage() {
        Set<Node> nodes = richTextArea.lookupAll(".text-flow");
        StringBuilder sb = new StringBuilder();
        int i = nodes.size();
        for (Node node : nodes) {
            ObservableList<Node> childrenNodes = ((TextFlow) node).getChildrenUnmodifiable();
            for (Node n : childrenNodes) {
                if (n instanceof Text text) {
                    sb.append(text.getText());
                }
                if (n instanceof ImageView imageView) {
                    String emojiUnified = (String) imageView.getProperties().get(EmojiImageUtils.IMAGE_VIEW_EMOJI_PROPERTY);
                    if (emojiUnified != null) {
                        Optional<Emoji> emojiOptional = EmojiData.emojiFromCodepoints(emojiUnified);
                        if (emojiOptional.isPresent()) {
                            Emoji emoji = emojiOptional.get();
                            sb.append(emoji.character());
                        }
                    } else {
                        String message = sb.toString();
                        if (!message.isEmpty()) {
                            sendMessage(message, MessageType.TEXT_MESSAGE);
                        }

                        String url = imageView.getImage().getUrl();
                        sendMessage(url, MessageType.IMAGE_MESSAGE);
                        sb = new StringBuilder();
                    }
                }
            }
            if (--i > 0) {
                sb.append(System.lineSeparator());
            }
        }
        if (!sb.toString().isEmpty()) {
            sendMessage(sb.toString(), MessageType.TEXT_MESSAGE);
        }
        richTextArea.getActionFactory().newDocument().execute(new ActionEvent());
    }

    private void sendMessage(Object object, MessageType messageType) {
        sessionEntity.setLastSendMsgUserId(ClientContextHolder.clientContext().id());
        ChatItemEntity selfChatItemEntity = ChatItemEntity.createSelfChatItemEntity();
        Long ackId = IdGen.genId();
        switch (messageType) {
            case TEXT_MESSAGE:
                String message = (String) object;
                Packet textPacket;
                if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                    textPacket = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId);
                } else {
                    textPacket = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                }
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(message);
                selfChatItemEntity.setContent(message);
                selfChatItemEntity.setMessageType(messageType);
                //发送消息
                send(textPacket);
                break;
            case IMAGE_MESSAGE:
                String path = (String) object;
                CompletableFuture<String> completableFuture = ChunkedUploader.uploadFile(path);
                completableFuture
                        .whenComplete((r, e) -> {
                            if (e != null){
                                log.error("upload chunk failed: ",e);
                                selfChatItemEntity.setImage(sendFailureImage);
                            }
                        })
                        .thenAccept(r -> {
                            log.info("upload completed; accessUrl: {}",r);
                            Packet imagePacket;
                            MessageMetadata messageMetadata = baseMessageMetadata(path);
                            Image image = new Image(path);
                            messageMetadata.setWidth(image.getWidth());
                            messageMetadata.setHeight(image.getHeight());
                            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                                imagePacket = new ImageMessage(r,messageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, false);
                            } else {
                                imagePacket = new ImageMessage(r,messageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                            }
                            //发送消息
                            send(imagePacket);
                        });
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(path);
                selfChatItemEntity.setContent(path);
                selfChatItemEntity.setMessageType(messageType);
                break;
        }
        addChatItem(selfChatItemEntity);
        selfChatItemEntity.setImage(sendingImage);
        ackChatItemEntityMap.put(ackId, selfChatItemEntity);
        //联动会话框
        chatVbox.fireEvent(new UserSessionEvent(UserSessionEvent.SEND_MESSAGE, sessionEntity));
        //异步等待消息回执
        EventLoop eventLoop = ClientContextHolder.clientContext().channel().eventLoop();
        RetryTask retryTask = new RetryTask();
        retryTask.setScheduledFuture(eventLoop.schedule(() -> {
            ChatItemEntity chatItemEntity = ackChatItemEntityMap.get(ackId);
            if (chatItemEntity != null) {
                chatItemEntity.setImage(sendFailureImage);
            }
            retryTaskMap.remove(ackId);
        }, 10, TimeUnit.SECONDS));
        retryTaskMap.put(ackId, retryTask);
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet) object;
        if (packet.getSender() != 0 && !packet.realSender().equals(sessionEntity.getReceiverUserId())) {
            return;
        }
        ChatItemEntity chatItemEntity = null;
        switch (packet.getHeader().getCmd()) {
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) packet;
                chatItemEntity = new ChatItemEntity();
                chatItemEntity.setContent(textMessage.getMessage());
                break;
            case IMAGE_MESSAGE:
                ImageMessage imageMessage = (ImageMessage) packet;
                chatItemEntity = new ChatItemEntity();
                chatItemEntity.setContent(imageMessage.getUrl());
                chatItemEntity.setMessageMetadata(imageMessage.getContentMetadata());
                break;
            case VIDEO_MESSAGE:
                VideoMessage videoMessage = (VideoMessage) packet;
                chatItemEntity = new ChatItemEntity();
                chatItemEntity.setContent(videoMessage.getUrl());
                chatItemEntity.setMessageMetadata(videoMessage.getContentMetadata());
                break;
            case MSG_RES:
                MessageStateResponse messageStateResponse = (MessageStateResponse) packet;
                ChatItemEntity cie = ackChatItemEntityMap.get(messageStateResponse.getAckId());
                if (cie != null) {
                    switch (messageStateResponse.getState()) {
                        case DELIVERED:
                            log.info("{},{}", messageStateResponse.getState(), messageStateResponse.getAckId());
                            cie.imageProperty().set(null);
                            ackChatItemEntityMap.remove(messageStateResponse.getAckId());
                            RetryTask retryTask = retryTaskMap.get(messageStateResponse.getAckId());
                            if (retryTask != null) {
                                retryTask.cancel();
                                retryTaskMap.remove(messageStateResponse.getAckId());
                            }
                            break;
                    }
                }
                break;
        }
        if (chatItemEntity == null) {
            return;
        }
        if (packet.isGroup()) {
            UserFriendInfo groupUserInfo = userGroupController.findGroupUserInfo(packet.getReceiver(), packet.getSender());
            String imageUrl = loadImageUrl(groupUserInfo.getAvatar());
            chatItemEntity.setAvatar(imageUrl);
            chatItemEntity.setName(groupUserInfo.getNickname());
        } else {
            chatItemEntity.setAvatar(sessionEntity.getAvatar());
            chatItemEntity.setName(sessionEntity.getName());
        }
        chatItemEntity.setSelf(false);
        chatItemEntity.setMessageType(MessageType.findMessageTypeByValue((int) packet.getCommand().getCmdCode()));
        chatItemEntity.setDeliveryMethod(packet.isGroup() ? DeliveryMethod.GROUP : DeliveryMethod.SINGLE);
        addChatItem(chatItemEntity);
    }

    private void setListView(List<ChatItemEntity> chatItemEntities) {
        ObservableList<ChatItemEntity> chatItemEntityObservableList = FXCollections.observableArrayList(chatItemEntities);
        chatListView.setItems(chatItemEntityObservableList);
    }

    private void addChatItem(SessionEntity sessionEntity) {
        ChatItemEntity chatItemEntity = new ChatItemEntity();
        chatItemEntity.setId(IdGen.genId());
        String imageUrl = loadImageUrl(sessionEntity.getLastUserAvatar());
        chatItemEntity.setAvatar(imageUrl);
        chatItemEntity.setName(sessionEntity.getLastUserName());
        if (ClientContextHolder.clientContext().id().equals(sessionEntity.getLastSendMsgUserId())) {
            chatItemEntity.setSelf(true);
        } else {
            chatItemEntity.setSelf(false);
        }
        chatItemEntity.setMessageType(sessionEntity.getLastMsgType());
        chatItemEntity.setContent(sessionEntity.getLastMsg());
        chatItemEntity.setDeliveryMethod(sessionEntity.getDeliveryMethod());
        chatItemEntity.setMessageMetadata(sessionEntity.getLastMessageMetadata());
        addChatItem(chatItemEntity);
    }

    private void addChatItem(ChatItemEntity chatItemEntity) {
        ObservableList<ChatItemEntity> items = chatListView.getItems();
        int index = items.size();
        items.addLast(chatItemEntity);
        Platform.runLater(() -> {
            chatListView.scrollTo(index);
        });
    }


    public void setUserFriendController(UserFriendController userFriendController) {
        this.userFriendController = userFriendController;
    }

    public void setUserGroupController(UserGroupController userGroupController) {
        this.userGroupController = userGroupController;
    }


    private MessageMetadata baseMessageMetadata(String path){
        if (path.startsWith("file:")) {
            path = path.substring(6);
        }
        File file = new File(path);
        MessageMetadata messageMetadata = new MessageMetadata();
        messageMetadata.setName(file.getName());
        long size = file.length();
        messageMetadata.setSize(size);
        String sizeDesc;
        if (size > 1048576){
            sizeDesc = String.format("%.1f",(double)size / (1024 * 1024)) + "M";
        }else if (size > 1024) {
            sizeDesc = String.format("%.1f",(double)size / 1024) + "K";
        } else {
            sizeDesc = size + "B";
        }
        messageMetadata.setSizeDesc(sizeDesc);
        try {
            messageMetadata.setMediaType(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            log.error("probeContentType error ",e);
            messageMetadata.setMediaType("Unknown media type");
        }
        return messageMetadata;
    }

}
