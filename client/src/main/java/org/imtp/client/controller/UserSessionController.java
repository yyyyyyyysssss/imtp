package org.imtp.client.controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.Tuple2;
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

    private Map<Long,Node> chatNodeMap;

    @Override
    protected void init0() {
        messageModel.pullUserSession();
        chatNodeMap = new HashMap<>();
        //会话框设置
        listView.setCellFactory(c -> new UserSessionListCell());
        listView.setStyle("-fx-selection-bar: lightgrey;");
        //设置鼠标监听
        listView.setOnMouseClicked(mouseEvent -> {
            SessionEntity sessionEntity = listView.getSelectionModel().getSelectedItem();
            Node node;
            if((node = chatNodeMap.get(sessionEntity.getId())) == null){
                Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
                Controller controller = tuple2.getV2();
                controller.initData(sessionEntity);

                node = tuple2.getV1();
                chatNodeMap.put(sessionEntity.getId(), node);
            }
            ObservableList<Node> children = chatPane.getChildren();
            if (!children.isEmpty()){
                children.removeLast();
            }
            children.addLast(node);
        });
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case FRIENDSHIP_RES:
                FriendshipResponse friendshipResponse = (FriendshipResponse)packet;
                List<UserFriendInfo> userFriendInfos = friendshipResponse.getUserFriendInfos();
                log.debug("userFriendInfos: {}", userFriendInfos);
                break;
            case GROUP_RELATIONSHIP_RES:
                GroupRelationshipResponse groupRelationshipResponse = (GroupRelationshipResponse) packet;
                List<UserGroupInfo> userGroupInfos = groupRelationshipResponse.getUserGroupInfos();
                log.debug("userGroupInfos: {}", userGroupInfos);
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
        }
    }

    private void setListView(List<SessionEntity> sessionEntities){
        Platform.runLater(() -> {
            ObservableList<SessionEntity> sessionEntityObservableList = listView.getItems();
            for (SessionEntity sessionEntity : sessionEntities) {
                Node node;
                if((node = chatNodeMap.get(sessionEntity.getId())) == null){
                    Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
                    Controller controller = tuple2.getV2();
                    controller.initData(sessionEntity);

                    node = tuple2.getV1();
                    chatNodeMap.put(sessionEntity.getId(), node);
                }
                ObservableList<Node> children = chatPane.getChildren();
                if (!children.isEmpty()){
                    children.removeLast();
                }
                children.addLast(node);

                sessionEntityObservableList.addLast(sessionEntity);
            }
        });
    }

    private SessionEntity convertSessionEntity(UserSessionInfo userSessionInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userSessionInfo.getName());
        String url;
        String avatar;
        if((avatar = userSessionInfo.getAvatar()) == null || !avatar.startsWith("classpath:")){
            url = ResourceUtils.classPathResource("/img/tmp.jpg").toExternalForm();
        }else {
            avatar = avatar.replace("classpath:","");
            url = ResourceUtils.classPathResource(avatar).toExternalForm();
        }
        sessionEntity.setAvatar(url);
        sessionEntity.setReceiverUserId(userSessionInfo.getReceiverUserId());
        sessionEntity.setTimestamp(userSessionInfo.getLastMsgTime());
        sessionEntity.setLastMsgType(userSessionInfo.getLastMsgType());
        sessionEntity.setLastMsg(userSessionInfo.getLastMsgContent());
        sessionEntity.setDeliveryMethod(userSessionInfo.getDeliveryMethod());
        return sessionEntity;
    }

}
