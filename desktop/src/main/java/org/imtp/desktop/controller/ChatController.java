package org.imtp.desktop.controller;


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
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.imtp.desktop.component.ChunkedUploader;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.entity.ChatItemEntity;
import org.imtp.desktop.entity.SessionEntity;
import org.imtp.desktop.enums.MessageStatus;
import org.imtp.desktop.event.EmojiEvent;
import org.imtp.desktop.event.UserSessionEvent;
import org.imtp.desktop.idwork.IdGen;
import org.imtp.desktop.util.ResourceUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
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


        //选择文件
        chatFileIcon.setOnMouseClicked(m -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(chatVbox.getScene().getWindow());
            if (file != null){
                String filePath = file.toURI().toString().replaceAll("%20"," ");
                MessageType messageType = messageTypeByPath(filePath);
                sendMessage(filePath, messageType);
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
                    String msg;
                    if ((msg = text.getText()).startsWith("file:")) {
                        MessageType messageType = messageTypeByPath(msg);
                        sendMessage(msg, messageType);
                    } else {
                        sb.append(msg);
                    }
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

    private MessageType messageTypeByPath(String path) {
        String mediaType = mediaType(path);
        if (mediaType.startsWith("video/")) {
            return MessageType.VIDEO_MESSAGE;
        } else if (mediaType.startsWith("image/")) {
            return MessageType.IMAGE_MESSAGE;
        } else {
            return MessageType.FILE_MESSAGE;
        }
    }

    private void sendMessage(Object object, MessageType messageType) {
        sessionEntity.setLastSendMsgUserId(ClientContextHolder.clientContext().id());
        ChatItemEntity selfChatItemEntity = ChatItemEntity.createSelfChatItemEntity();
        Long ackId = IdGen.genId();
        Packet packet = null;
        switch (messageType) {
            case TEXT_MESSAGE:
                String message = (String) object;
                if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                    packet = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId);
                } else {
                    packet = new TextMessage(message, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                }
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(message);
                selfChatItemEntity.setContent(message);
                selfChatItemEntity.setMessageType(messageType);
                //发送
                sendMessage(packet,selfChatItemEntity,ackId);
                break;
            case IMAGE_MESSAGE:
                String imagePath = (String) object;
                MessageMetadata imageMessageMetadata = baseMessageMetadata(imagePath);
                Image image = new Image(imagePath);
                imageMessageMetadata.setWidth(image.getWidth());
                imageMessageMetadata.setHeight(image.getHeight());
                CompletableFuture<String> imageCompletableFuture = ChunkedUploader.uploadFile(imagePath);
                imageCompletableFuture
                        .whenComplete((r, e) -> {
                            if (e != null) {
                                log.error("upload chunk failed: ", e);
                                selfChatItemEntity.setImage(sendFailureImage);
                                Platform.runLater(() -> {
                                    selfChatItemEntity.messageStatusProperty().set(MessageStatus.FAILED);
                                });
                            }
                        })
                        .thenAccept(r -> {
                            log.info("upload completed; accessUrl: {}", r);
                            Packet imagePacket;
                            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                                imagePacket = new ImageMessage(r, imageMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, false);
                            } else {
                                imagePacket = new ImageMessage(r, imageMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                            }
                            selfChatItemEntity.messageStatusProperty().set(MessageStatus.SENT);
                            //发送消息
                            send(imagePacket);
                        });
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(imagePath);
                selfChatItemEntity.setMessageStatus(MessageStatus.PENDING);
                selfChatItemEntity.setContent(imagePath);
                selfChatItemEntity.setMessageMetadata(imageMessageMetadata);
                selfChatItemEntity.setMessageType(messageType);
                //发送
                sendMessage(null,selfChatItemEntity,ackId);
                break;
            case VIDEO_MESSAGE:
                String videoPath = (String) object;
                MessageMetadata videoMessageMetadata = baseMessageMetadata(videoPath);
                try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath.substring(6))){
                    grabber.start();
                    int width = grabber.getImageWidth();
                    int height = grabber.getImageHeight();
                    long duration = grabber.getLengthInTime() / 1000000;
                    grabber.setFrameNumber(1);
                    Frame frame = grabber.grabImage();
                    Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                    BufferedImage bi = java2DFrameConverter.convert(frame);
                    Java2DFrameConverter.copy(frame, bi);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bi,"png",byteArrayOutputStream);
                    selfChatItemEntity.setSelfVideoThumbnailImage(new Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));

                    int m = (int) Math.floor((double) duration / 60);
                    int s = (int) Math.floor(duration % 60);
                    String durationDesc = String.format("%02d",m) + ":" + String.format("%02d",s);
                    log.info("width:{} height:{} duration:{}",width,height,durationDesc);
                    videoMessageMetadata.setWidth(width * 1.0);
                    videoMessageMetadata.setHeight(height * 1.0);
                    videoMessageMetadata.setDuration(duration);
                    videoMessageMetadata.setDurationDesc(durationDesc);

                    CompletableFuture<String> videoCompletableFuture = ChunkedUploader.uploadFile(videoPath);
                    videoCompletableFuture
                            .whenComplete((r, e) -> {
                                if (e != null) {
                                    log.error("upload chunk failed: ", e);
                                    selfChatItemEntity.setImage(sendFailureImage);
                                    Platform.runLater(() -> {
                                        selfChatItemEntity.messageStatusProperty().set(MessageStatus.FAILED);
                                    });
                                }
                            })
                            .thenAccept(r -> {
                                log.info("upload completed; accessUrl: {}", r);
                                String fileName = UUID.randomUUID().toString().replaceAll("-","") + ".png";
                                ChunkedUploader.uploadFile(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),fileName,"image/png")
                                        .thenAccept(ru -> {
                                            log.info("thumbnailUrl: {} ",ru);
                                            videoMessageMetadata.setThumbnailUrl(ru);
                                            Packet videoPacket;
                                            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                                                videoPacket = new VideoMessage(r, videoMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, false);
                                            } else {
                                                videoPacket = new VideoMessage(r, videoMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                                            }
                                            selfChatItemEntity.messageStatusProperty().set(MessageStatus.SENT);
                                            //发送消息
                                            send(videoPacket);
                                        });
                            });

                    sessionEntity.setLastMsgType(messageType);
                    sessionEntity.setLastMsg(videoPath);
                    selfChatItemEntity.setMessageStatus(MessageStatus.PENDING);
                    selfChatItemEntity.setContent(videoPath);
                    selfChatItemEntity.setMessageMetadata(videoMessageMetadata);
                    selfChatItemEntity.setMessageType(messageType);
                    //发送
                    sendMessage(null,selfChatItemEntity,ackId);

                    java2DFrameConverter.close();
                    grabber.stop();
                }catch (Exception exception){
                    log.error("javacv error: ",exception);
                }
                break;
            case FILE_MESSAGE:
                String filePath = (String) object;
                MessageMetadata fileMessageMetadata = baseMessageMetadata(filePath);
                CompletableFuture<String> fileCompletableFuture = ChunkedUploader.uploadFile(filePath);
                fileCompletableFuture
                        .whenComplete((r, e) -> {
                            if (e != null) {
                                log.error("upload chunk failed: ", e);
                                selfChatItemEntity.setImage(sendFailureImage);
                            }
                        })
                        .thenAccept(r -> {
                            log.info("upload completed; accessUrl: {}", r);
                            Packet imagePacket;
                            if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)) {
                                imagePacket = new FileMessage(r, fileMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, false);
                            } else {
                                imagePacket = new FileMessage(r, fileMessageMetadata, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(), ackId, true);
                            }
                            //发送消息
                            send(imagePacket);
                        });
                sessionEntity.setLastMsgType(messageType);
                sessionEntity.setLastMsg(filePath);
                selfChatItemEntity.setContent(filePath);
                selfChatItemEntity.setMessageMetadata(fileMessageMetadata);
                selfChatItemEntity.setMessageType(messageType);
                //发送
                sendMessage(null,selfChatItemEntity,ackId);
        }
    }

    private void sendMessage(Packet packet,ChatItemEntity selfChatItemEntity,Long ackId){
        addChatItem(selfChatItemEntity);
        selfChatItemEntity.setImage(sendingImage);
        ackChatItemEntityMap.put(ackId, selfChatItemEntity);
        if (packet != null){
            //发送消息
            send(packet);
        }
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
            case FILE_MESSAGE:
                FileMessage fileMessage = (FileMessage) packet;
                chatItemEntity = new ChatItemEntity();
                chatItemEntity.setContent(fileMessage.getUrl());
                chatItemEntity.setMessageMetadata(fileMessage.getContentMetadata());
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


    private MessageMetadata baseMessageMetadata(String path) {
        if (path.startsWith("file:")) {
            path = path.substring(6);
        }
        File file = new File(path);
        MessageMetadata messageMetadata = new MessageMetadata();
        messageMetadata.setName(file.getName());
        long size = file.length();
        messageMetadata.setSize(size);
        String sizeDesc;
        if (size > 1048576) {
            sizeDesc = String.format("%.1f", (double) size / (1024 * 1024)) + "M";
        } else if (size > 1024) {
            sizeDesc = String.format("%.1f", (double) size / 1024) + "K";
        } else {
            sizeDesc = size + "B";
        }
        messageMetadata.setSizeDesc(sizeDesc);
        String mediaType = mediaType(file);
        messageMetadata.setMediaType(mediaType);
        return messageMetadata;
    }

    private String mediaType(String path) {
        if (path.startsWith("file:")) {
            path = path.substring(6);
        }
        return mediaType(new File(path));
    }

    private String mediaType(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("probeContentType error ", e);
            return "unknown";
        }
    }

}
