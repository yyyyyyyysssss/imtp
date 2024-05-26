package org.imtp.client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.component.ImageUrlParse;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.FriendEntity;
import org.imtp.client.util.Tuple2;
import org.imtp.common.packet.FriendshipResponse;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserFriendController extends AbstractController{

    @FXML
    private ListView<FriendEntity> friendListView;

    @FXML
    private Pane friendPane;

    private ImageUrlParse imageUrlParse;

    private Map<Long,Node> userFriendNodeMap;

    @FXML
    public void initialize(){
        imageUrlParse = new ClassPathImageUrlParse();
        userFriendNodeMap = new HashMap<>();

        friendListView.setCellFactory(c -> new UserFriendListCell());

        friendListView.setOnMouseClicked(mouseEvent -> {
            FriendEntity friendEntity = friendListView.getSelectionModel().getSelectedItem();
            if (friendEntity == null){
                return;
            }
            Node node;
            if (( node = userFriendNodeMap.get(friendEntity.getId())) == null){
                node = addFriendDetailsNode(friendEntity);
            }
            ObservableList<Node> children = friendPane.getChildren();
            if (!children.isEmpty()){
                children.removeLast();
            }
            children.addLast(node);
        });

    }

    @Override
    protected void init0() {
        this.messageModel.pullFriendship();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case FRIENDSHIP_RES:
                FriendshipResponse friendshipResponse = (FriendshipResponse)packet;
                List<UserFriendInfo> userFriendInfos = friendshipResponse.getUserFriendInfos();
                log.info("userFriendInfos: {}", userFriendInfos);
                if (!userFriendInfos.isEmpty()){
                    List<FriendEntity> friendEntities = userFriendInfos.stream().map(this::convertFriendEntity).toList();
                    setListView(friendEntities);
                }
                break;
        }
    }


    private void setListView(List<FriendEntity> friendEntities){
        for (FriendEntity friendEntity : friendEntities){
            addUserFriendNode(friendEntity);
        }
    }

    private void addUserFriendNode(FriendEntity friendEntity){
        addUserFriendNode(friendEntity,false);
    }

    private void addUserFriendNode(FriendEntity friendEntity, boolean selected){
        friendListView.getItems().addFirst(friendEntity);
        if (selected){
            friendListView.getSelectionModel().select(friendEntity);
        }
    }

    //添加好友关联的详情
    private Node addFriendDetailsNode(FriendEntity friendEntity){
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.USER_FRIEND_DETAILS_FML);
        Controller controller = tuple2.getV2();
        controller.initData(friendEntity);
        Node node = tuple2.getV1();
        userFriendNodeMap.put(friendEntity.getId(), node);
        return node;
    }

    private FriendEntity convertFriendEntity(UserFriendInfo userFriendInfo){
        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setId(userFriendInfo.getId());
        friendEntity.setName(userFriendInfo.getNickname());
        friendEntity.setAccount(userFriendInfo.getAccount());
        friendEntity.setGender(userFriendInfo.getGender());
        String url = imageUrlParse.loadUrl(userFriendInfo.getAvatar());
        friendEntity.setAvatar(url);
        return friendEntity;
    }

}
