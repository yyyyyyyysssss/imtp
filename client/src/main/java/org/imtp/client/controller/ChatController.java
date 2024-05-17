package org.imtp.client.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.entity.SessionEntity;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;

import java.util.List;
import java.util.Random;

@Slf4j
public class ChatController extends AbstractController{

    @FXML
    private ListView<SessionEntity> listView;

    @FXML
    private TextArea inputText;

    @Override
    protected void init0() {
//        messageModel.pullFriendship();
//        messageModel.pullGroupRelationship();
        messageModel.pullOfflineMessage();
        messageModel.pullUserSession();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case FRIENDSHIP_RES:
                FriendshipResponse friendshipResponse = (FriendshipResponse)packet;
                List<UserFriendInfo> userFriendInfos = friendshipResponse.getUserFriendInfos();
                if(userFriendInfos != null && !userFriendInfos.isEmpty()){
                    List<SessionEntity> sessionEntities = userFriendInfos.stream().map(this::convertSessionEntity).toList();
                    setListView(sessionEntities);
                }
                break;
            case GROUP_RELATIONSHIP_RES:
                GroupRelationshipResponse groupRelationshipResponse = (GroupRelationshipResponse) packet;
                List<UserGroupInfo> userGroupInfos = groupRelationshipResponse.getUserGroupInfos();
                System.out.println(userGroupInfos);
                break;
            case OFFLINE_MSG_RES:
                OfflineMessageResponse offlineMessageResponse = (OfflineMessageResponse) packet;
                List<OfflineMessageInfo> offlineMessageInfos = offlineMessageResponse.getOfflineMessageInfos();
                System.out.println(offlineMessageInfos);
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
                TextMessage textMessage = (TextMessage) packet;
                if(textMessage.isGroup()){
                    System.out.println("*用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }else {
                    System.out.println("用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }
                break;
        }
    }

    private void setListView(List<SessionEntity> sessionEntities){
        listView.setCellFactory(c -> new UserSessionListCell());
        ObservableList<SessionEntity> sessionEntityObservableList = FXCollections.observableArrayList(sessionEntities);
        listView.setItems(sessionEntityObservableList);
        //默认选中第一项
        listView.getSelectionModel().selectFirst();
        listView.setStyle("-fx-selection-bar: lightgrey;");
        //设置鼠标监听
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                SessionEntity sessionEntity = listView.getSelectionModel().getSelectedItem();
//                sessionEntityObservableList.addLast(sessionEntity);
            }
        });
    }

    private SessionEntity convertSessionEntity(UserFriendInfo userFriendInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userFriendInfo.getNickname());
        sessionEntity.setAvatar(userFriendInfo.getAvatar());
        sessionEntity.setReceiverId(userFriendInfo.getId());
        sessionEntity.setTimestamp(System.currentTimeMillis());
        return sessionEntity;
    }

    private SessionEntity convertSessionEntity(UserSessionInfo userSessionInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userSessionInfo.getName());
        sessionEntity.setAvatar(userSessionInfo.getAvatar());
        sessionEntity.setReceiverId(userSessionInfo.getId());
        sessionEntity.setTimestamp(userSessionInfo.getLastMsgTime());
        sessionEntity.setLastMsgType(userSessionInfo.getLastMsgType());
        sessionEntity.setLastMsg(userSessionInfo.getLastMsgContent());
        return sessionEntity;
    }

}
