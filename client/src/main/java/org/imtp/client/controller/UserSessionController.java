package org.imtp.client.controller;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.event.UserSessionEvent;
import org.imtp.client.util.Tuple2;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.enums.MessageType;
import org.imtp.common.packet.AbstractTextMessage;
import org.imtp.common.packet.OfflineMessageResponse;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.UserSessionResponse;
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

    private HomeController homeController;

    private UserFriendController userFriendController;

    private UserGroupController userGroupController;

    //会话与聊天框的对应关系
    private Map<Long,Node> userSessionChatNodeMap;
    //会话与会话项数据对应关系
    private Map<Long,SessionEntity> userSessionEntityMap;

    @FXML
    public void initialize(){
        userSessionChatNodeMap = new HashMap<>();
        userSessionEntityMap = new HashMap<>();
        //会话框设置
        listView.setCellFactory(c -> new UserSessionListCell());
        listView.setFocusTraversable(false);
        //设置鼠标监听
        listView.setOnMouseClicked(mouseEvent -> {
            SessionEntity sessionEntity = listView.getSelectionModel().getSelectedItem();
            if (sessionEntity == null){
                return;
            }
            showChatNode(sessionEntity);
            sessionEntity.setCount("");
        });
    }

    @Override
    protected void init0() {
        //拉取用户会话
        messageModel.pullUserSession();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        SessionEntity sessionEntity = null;
        switch (packet.getHeader().getCmd()){
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
            case TEXT_MESSAGE,IMAGE_MESSAGE:
                Long sender = packet.realSender();
                sessionEntity = userSessionEntityMap.get(sender);
                AbstractTextMessage textMessage = (TextMessage) packet;
                if (sessionEntity == null){
                    sessionEntity = createUserSessionByPacket(textMessage);
                    //添加会话项
                    addUserSessionNode(sessionEntity,true,false);
                    //添加会话关联的聊天框
                    addChatNode(sessionEntity);
                }else {
                    if (userSessionChatNodeMap.get(sender) ==null){
                        //添加会话关联的聊天框
                        addChatNode(sessionEntity);
                    }
                    sessionEntity.setLastSendMsgUserId(packet.getSender());
                    sessionEntity.setLastMsg(textMessage.getMessage());
                    if (packet.isGroup()){
                        UserFriendInfo groupUserInfo = userGroupController.findGroupUserInfo(packet.getReceiver(), packet.getSender());
                        sessionEntity.setLastUserName(groupUserInfo.getNickname());
                    }
                    updateUserSessionNode(sessionEntity);
                }
                break;
        }
        messageCount(sessionEntity);
    }

    private void messageCount(SessionEntity sessionEntity){
        Platform.runLater(() -> {
            if (sessionEntity != null && !sessionEntity.equals(listView.getSelectionModel().getSelectedItem())){
                String currentCount = sessionEntity.getCount();
                if (currentCount == null || currentCount.isEmpty()){
                    sessionEntity.countProperty().set("1");
                }else {
                    sessionEntity.setCount(String.valueOf(Long.parseLong(currentCount) + 1));
                }
            }
        });
    }

    public void setUserFriendController(UserFriendController userFriendController) {
        this.userFriendController = userFriendController;
    }

    public void setUserGroupController(UserGroupController userGroupController) {
        this.userGroupController = userGroupController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void showChatNode(SessionEntity sessionEntity){
        Node node;
        if((node = userSessionChatNodeMap.get(sessionEntity.getReceiverUserId())) == null){
            node = addChatNode(sessionEntity);
        }
        showChatNode(node);
    }

    private void showChatNode(Node node){
        ObservableList<Node> children = chatPane.getChildren();
        if (!children.isEmpty()){
            children.removeLast();
        }
        children.addLast(node);
    }

    private void setListView(List<SessionEntity> sessionEntities){
        for (SessionEntity sessionEntity : sessionEntities) {
            //添加会话项
            addUserSessionNode(sessionEntity,false,false);
            //添加会话关联的聊天框
            addChatNode(sessionEntity);
        }
    }

    private void addUserSessionNode(SessionEntity sessionEntity,boolean addFirst, boolean selected){
        if (addFirst){
            listView.getItems().addFirst(sessionEntity);
        }else {
            listView.getItems().add(sessionEntity);
        }
        if (selected){
            listView.getSelectionModel().clearSelection();
            listView.getSelectionModel().select(sessionEntity);
        }
        userSessionEntityMap.put(sessionEntity.getReceiverUserId(),sessionEntity);
    }

    public void addUserSessionAndChatNode(UserGroupInfo userGroupInfo){
        if (userGroupInfo == null){
            throw new NullPointerException("userGroupInfo is null");
        }
        SessionEntity sessionEntity = userSessionEntityMap.get(userGroupInfo.getId());
        Node node = null;
        if (sessionEntity == null){
            SessionEntity se = createUserSessionByUserGroupInfo(userGroupInfo);
            addUserSessionNode(se,true,true);
            //添加会话关联的聊天框
            node = addChatNode(se);
        }else {
            if ((node = userSessionChatNodeMap.get(userGroupInfo.getId())) ==null){
                //添加会话关联的聊天框
                node = addChatNode(sessionEntity);
            }
            updateUserSessionNode(sessionEntity,true);
        }
        showChatNode(node);
    }

    public void addUserSessionAndChatNode(UserFriendInfo userFriendInfo){
        if (userFriendInfo == null){
            throw new NullPointerException("userFriendInfo is null");
        }
        SessionEntity sessionEntity = userSessionEntityMap.get(userFriendInfo.getId());
        Node node = null;
        if (sessionEntity == null){
            SessionEntity se = createUserSessionByUserFriendInfo(userFriendInfo);
            addUserSessionNode(se,true,true);
            //添加会话关联的聊天框
            node = addChatNode(se);
        }else {
            if ((node = userSessionChatNodeMap.get(userFriendInfo.getId())) ==null){
                //添加会话关联的聊天框
                node = addChatNode(sessionEntity);
            }
            updateUserSessionNode(sessionEntity,true);
        }
        showChatNode(node);
    }

    private void updateUserSessionNode(SessionEntity sessionEntity){
        updateUserSessionNode(sessionEntity,false);
    }

    private void updateUserSessionNode(SessionEntity sessionEntity,boolean selected){
        SessionEntity selectedItem = listView.getSelectionModel().getSelectedItem();
        ObservableList<SessionEntity> listViewItems = listView.getItems();
        listViewItems.remove(sessionEntity);
        if (selected){
            addUserSessionNode(sessionEntity,true,true);
        }else {
            if (selectedItem != null && selectedItem.getId().equals(sessionEntity.getId())){
                addUserSessionNode(sessionEntity,true,true);
            }else {
                addUserSessionNode(sessionEntity,true,false);
            }
        }
    }

    //添加会话关联的聊天框
    private Node addChatNode(SessionEntity sessionEntity){
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.CHAT_FML);
        ChatController controller = (ChatController)tuple2.getV2();
        controller.setUserFriendController(userFriendController);
        controller.setUserGroupController(userGroupController);
        controller.initData(sessionEntity);
        Node node = tuple2.getV1();
        //注册发送消息事件，将其置顶
        node.addEventHandler(UserSessionEvent.SEND_MESSAGE, sessionEvent -> {
            updateUserSessionNode(sessionEvent.getSessionEntity());
        });
        userSessionChatNodeMap.put(sessionEntity.getReceiverUserId(), node);
        return node;
    }

    private SessionEntity convertSessionEntity(UserSessionInfo userSessionInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setName(userSessionInfo.getName());
        String url = loadImageUrl(userSessionInfo.getAvatar());
        sessionEntity.setAvatar(url);
        sessionEntity.setReceiverUserId(userSessionInfo.getReceiverUserId());
        sessionEntity.setTimestamp(userSessionInfo.getLastMsgTime());
        sessionEntity.setLastMsgType(userSessionInfo.getLastMsgType());
        sessionEntity.setLastMsg(userSessionInfo.getLastMsgContent());
        sessionEntity.setDeliveryMethod(userSessionInfo.getDeliveryMethod());
        sessionEntity.setLastSendMsgUserId(userSessionInfo.getLastSendMsgUserId());
        sessionEntity.setLastUserAvatar(userSessionInfo.getLastUserAvatar());
        sessionEntity.setLastUserName(userSessionInfo.getLastUserName());
        return sessionEntity;
    }


    private SessionEntity createUserSessionByUserFriendInfo(UserFriendInfo userFriendInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setReceiverUserId(userFriendInfo.getId());
        sessionEntity.setName(userFriendInfo.getNickname());
        String url = loadImageUrl(userFriendInfo.getAvatar());
        sessionEntity.setAvatar(url);
        sessionEntity.setDeliveryMethod(DeliveryMethod.SINGLE);
        return sessionEntity;
    }

    private SessionEntity createUserSessionByUserGroupInfo(UserGroupInfo userGroupInfo){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        sessionEntity.setReceiverUserId(userGroupInfo.getId());
        sessionEntity.setName(userGroupInfo.getGroupName());
        String url = loadImageUrl(userGroupInfo.getAvatar());
        sessionEntity.setAvatar(url);
        sessionEntity.setDeliveryMethod(DeliveryMethod.GROUP);
        return sessionEntity;
    }

    private SessionEntity createUserSessionByPacket(Packet packet){
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(new Random().nextLong());
        Long sender = packet.realSender();
        sessionEntity.setReceiverUserId(sender);
        if(packet.isGroup()){
            UserGroupInfo userGroupInfo = userGroupController.findUserGroupInfo(sender);
            sessionEntity.setName(userGroupInfo.getGroupName());
            sessionEntity.setAvatar(loadImageUrl(userGroupInfo.getAvatar()));
            sessionEntity.setDeliveryMethod(DeliveryMethod.GROUP);
            UserFriendInfo groupUserInfo = userGroupController.findGroupUserInfo(packet.getReceiver(), packet.getSender());
            sessionEntity.setLastUserAvatar(loadImageUrl(groupUserInfo.getAvatar()));
            sessionEntity.setLastUserName(groupUserInfo.getNickname());
        }else {
            sessionEntity.setReceiverUserId(sender);
            UserFriendInfo userFriendInfo = userFriendController.findUserFriendInfo(sender);
            sessionEntity.setName(userFriendInfo.getNickname());
            String url = loadImageUrl(userFriendInfo.getAvatar());
            sessionEntity.setAvatar(url);
            sessionEntity.setDeliveryMethod(DeliveryMethod.SINGLE);
            sessionEntity.setLastUserAvatar(url);
            sessionEntity.setLastUserName(userFriendInfo.getNickname());
        }
        MessageType messageType = MessageType.findMessageTypeByValue((int) packet.getCommand().getCmdCode());
        sessionEntity.setLastMsgType(messageType);
        sessionEntity.setLastSendMsgUserId(packet.getSender());
        if (messageType.equals(MessageType.TEXT_MESSAGE)){
            sessionEntity.setLastMsg(((TextMessage)packet).getMessage());
        }
        //TODO 消息时间
        return sessionEntity;
    }

}
