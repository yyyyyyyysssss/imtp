package org.imtp.client.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.Tuple2;
import org.imtp.common.packet.FriendshipResponse;
import org.imtp.common.packet.GroupRelationshipResponse;
import org.imtp.common.packet.OfflineMessageResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;

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
        messageModel.pullFriendship();
        messageModel.pullGroupRelationship();
        messageModel.pullOfflineMessage();
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
        listView.setCellFactory(new Callback<ListView<SessionEntity>, ListCell<SessionEntity>>() {
            @Override
            public ListCell<SessionEntity> call(ListView<SessionEntity> sessionEntityListView) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(SessionEntity sessionEntity, boolean b) {
                        super.updateItem(sessionEntity,b);
                        if(sessionEntity != null){
                            Tuple2<Node, Controller> tuple2 = loadNodeByPath(FXMLResourceConstant.USER_SESSION_ITEM_FML);
                            //临时头像测试
                            String url = ResourceUtils.classPathResource("/img/tmp.jpg").toExternalForm();
                            sessionEntity.setAvatar(url);
                            sessionEntity.setLastMsg("好的吧");
                            Node node = tuple2.getV1();
                            Controller controller = tuple2.getV2();
                            controller.initData(sessionEntity);
                            setGraphic(node);
                        }else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
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
                System.out.println(sessionEntity);
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

    private SessionEntity convertSessionEntity(UserGroupInfo userGroupInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userGroupInfo.getGroupName());
        sessionEntity.setAvatar(userGroupInfo.getAvatar());
        sessionEntity.setReceiverId(userGroupInfo.getId());
        sessionEntity.setTimestamp(System.currentTimeMillis());
        return sessionEntity;
    }

}
