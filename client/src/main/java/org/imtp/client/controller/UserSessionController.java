package org.imtp.client.controller;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.component.ImageUrlParse;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.util.Tuple2;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
public class UserSessionController extends AbstractController{

    @FXML
    private Pane chatPane;

    @FXML
    private ListView<SessionEntity> listView;

    private Map<Long,Node> userSessionNodeMap;

    private ImageUrlParse imageUrlParse;

    //用户好友缓存
    private Map<Long,UserFriendInfo> userFriendInfoMap;
    //用户群组
    private Map<Long,UserGroupInfo> userGroupInfoMap;

    @Override
    protected void init0() {
        imageUrlParse = new ClassPathImageUrlParse();

        userSessionNodeMap = new HashMap<>();

        userFriendInfoMap = new HashMap<>();
        userGroupInfoMap = new HashMap<>();

        //会话框设置
        listView.setCellFactory(c -> new UserSessionListCell());
        listView.setStyle("-fx-selection-bar: lightgrey;");
        //设置鼠标监听
        listView.setOnMouseClicked(mouseEvent -> {
            SessionEntity sessionEntity = listView.getSelectionModel().getSelectedItem();
            Node node;
            if((node = userSessionNodeMap.get(sessionEntity.getReceiverUserId())) == null){
                Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
                Controller controller = tuple2.getV2();
                controller.initData(sessionEntity);

                node = tuple2.getV1();
                userSessionNodeMap.put(sessionEntity.getReceiverUserId(), node);
            }
            ObservableList<Node> children = chatPane.getChildren();
            if (!children.isEmpty()){
                children.removeLast();
            }
            children.addLast(node);
        });

        messageModel.pullUserSession();
        messageModel.pullFriendship();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case FRIENDSHIP_RES:
                FriendshipResponse friendshipResponse = (FriendshipResponse)packet;
                List<UserFriendInfo> userFriendInfos = friendshipResponse.getUserFriendInfos();
                if (!userFriendInfos.isEmpty()){
                    for (UserFriendInfo userFriendInfo : userFriendInfos){
                        userFriendInfoMap.put(userFriendInfo.getId(), userFriendInfo);
                    }
                }
                break;
            case GROUP_RELATIONSHIP_RES:
                GroupRelationshipResponse groupRelationshipResponse = (GroupRelationshipResponse) packet;
                List<UserGroupInfo> userGroupInfos = groupRelationshipResponse.getUserGroupInfos();
                if (!userGroupInfos.isEmpty()){
                    for (UserGroupInfo userGroupInfo : userGroupInfos){
                        userGroupInfoMap.put(userGroupInfo.getId(), userGroupInfo);
                    }
                }
                break;
            case OFFLINE_MSG_RES:
                OfflineMessageResponse offlineMessageResponse = (OfflineMessageResponse) packet;
                List<OfflineMessageInfo> offlineMessageInfos = offlineMessageResponse.getOfflineMessageInfos();
                log.debug("offlineMessageInfos: {}", offlineMessageInfos);
                break;
            case USER_SESSION_RES:
                UserSessionResponse userSessionResponse = (UserSessionResponse)packet;
                List<UserSessionInfo> userSessionInfos = userSessionResponse.getUserSessionInfos();
                if(userSessionInfos != null && !userSessionInfos.isEmpty()){
                    List<SessionEntity> sessionEntities = userSessionInfos.stream().map(this::convertSessionEntity).toList();
                    setListView(sessionEntities);
                }
                break;
            case TEXT_MESSAGE:
                Long sender = packet.getSender();
                Node node = userSessionNodeMap.get(sender);
                if (node == null){
                    TextMessage textMessage = (TextMessage) packet;
                    SessionEntity sessionEntity = createUserSessionByPacket(textMessage);
                    addUserSessionNode(sessionEntity);
                }
                break;
        }
    }

    private void setListView(List<SessionEntity> sessionEntities){
        Platform.runLater(() -> {
            ObservableList<SessionEntity> sessionEntityObservableList = listView.getItems();
            for (SessionEntity sessionEntity : sessionEntities) {
                Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
                Node node = tuple2.getV1();
                Controller controller = tuple2.getV2();
                controller.initData(sessionEntity);
                userSessionNodeMap.put(sessionEntity.getReceiverUserId(), node);
                sessionEntityObservableList.addLast(sessionEntity);
            }
        });
    }

    private void addUserSessionNode(SessionEntity sessionEntity){
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
        Controller controller = tuple2.getV2();
        controller.initData(sessionEntity);
        Node node = tuple2.getV1();
        userSessionNodeMap.put(sessionEntity.getReceiverUserId(), node);
        listView.getItems().addLast(sessionEntity);
    }

    private SessionEntity convertSessionEntity(UserSessionInfo userSessionInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userSessionInfo.getName());
        String url = imageUrlParse.loadUrl(userSessionInfo.getAvatar());
        sessionEntity.setAvatar(url);
        sessionEntity.setReceiverUserId(userSessionInfo.getReceiverUserId());
        sessionEntity.setTimestamp(userSessionInfo.getLastMsgTime());
        sessionEntity.setLastMsgType(userSessionInfo.getLastMsgType());
        sessionEntity.setLastMsg(userSessionInfo.getLastMsgContent());
        sessionEntity.setDeliveryMethod(userSessionInfo.getDeliveryMethod());
        return sessionEntity;
    }

    private SessionEntity createUserSessionByPacket(Packet packet){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        Long sender = packet.getSender();
        sessionEntity.setReceiverUserId(sender);
        if(packet.isGroup()){
            UserGroupInfo userGroupInfo = userGroupInfoMap.get(sender);
            sessionEntity.setName(userGroupInfo.getGroupName());
            String url = imageUrlParse.loadUrl(userGroupInfo.getAvatar());
            sessionEntity.setAvatar(url);
            sessionEntity.setDeliveryMethod(DeliveryMethod.GROUP);
        }else {
            UserFriendInfo userFriendInfo = userFriendInfoMap.get(sender);
            sessionEntity.setName(userFriendInfo.getNickname());
            String url = imageUrlParse.loadUrl(userFriendInfo.getAvatar());
            sessionEntity.setAvatar(url);
            sessionEntity.setDeliveryMethod(DeliveryMethod.SINGLE);
        }
        MessageType messageType = MessageType.findMessageTypeByValue((int) packet.getCommand().getCmdCode());
        sessionEntity.setLastMsgType(messageType);
        if (messageType.equals(MessageType.TEXT_MESSAGE)){
            sessionEntity.setLastMsg(((TextMessage)packet).getMessage());
        }
        //TODO 消息时间
        return sessionEntity;
    }

}
