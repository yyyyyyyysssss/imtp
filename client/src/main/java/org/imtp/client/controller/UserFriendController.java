package org.imtp.client.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.Callback;
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
public class UserFriendController extends AbstractController implements Callback<Long> {

    @FXML
    private HBox userFriendHBox;

    @FXML
    private ListView<FriendEntity> friendListView;

    @FXML
    private BorderPane friendPane;

    private HomeController homeController;

    private UserSessionController userSessionController;

    private Map<Long,Node> userFriendNodeMap;

    //用户好友缓存
    private Map<Long,UserFriendInfo> userFriendInfoMap;

    @FXML
    public void initialize(){
        userFriendNodeMap = new HashMap<>();
        userFriendInfoMap = new HashMap<>();

        friendListView.setCellFactory(c -> new UserFriendListCell());
        friendListView.setFocusTraversable(false);
        friendListView.setOnMouseClicked(mouseEvent -> {
            FriendEntity friendEntity = friendListView.getSelectionModel().getSelectedItem();
            if (friendEntity == null){
                return;
            }
            Node node;
            if (( node = userFriendNodeMap.get(friendEntity.getId())) == null){
                node = addFriendDetailsNode(friendEntity);
            }

            friendPane.setCenter(node);
        });

        friendListView.prefHeightProperty().bind(userFriendHBox.heightProperty());
        friendPane.prefHeightProperty().bind(userFriendHBox.heightProperty());

        userFriendHBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                friendPane.setPrefWidth(t1.doubleValue() - friendListView.getPrefWidth());
            }
        });

    }

    public void setUserSessionController(UserSessionController userSessionController) {
        this.userSessionController = userSessionController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @Override
    protected void init0() {
        //拉取用户好友关系
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
                        setListView(convertFriendEntity(userFriendInfo));
                        userFriendInfoMap.put(userFriendInfo.getId(), userFriendInfo);
                    }
                }
                break;
        }
    }

    private void setListView(List<FriendEntity> friendEntities){
        for (FriendEntity friendEntity : friendEntities){
            setListView(friendEntity);
        }
    }

    private void setListView(FriendEntity friendEntity){
        addUserFriendNode(friendEntity);
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

    public UserFriendInfo findUserFriendInfo(Long id){

        return userFriendInfoMap.get(id);
    }

    //添加好友关联的详情
    private Node addFriendDetailsNode(FriendEntity friendEntity){
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.USER_FRIEND_DETAILS_FML);
        UserFriendDetailsController controller = (UserFriendDetailsController)tuple2.getV2();
        controller.initData(friendEntity);
        controller.setCallback(this);
        Node node = tuple2.getV1();
        userFriendNodeMap.put(friendEntity.getId(), node);
        return node;
    }

    @Override
    public void callback(Long userId) {
        UserFriendInfo userFriendInfo = userFriendInfoMap.get(userId);
        homeController.switchUserSession();
        userSessionController.addUserSessionAndChatNode(userFriendInfo);
    }

    private FriendEntity convertFriendEntity(UserFriendInfo userFriendInfo){
        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setId(userFriendInfo.getId());
        friendEntity.setName(userFriendInfo.getNickname());
        friendEntity.setAccount(userFriendInfo.getAccount());
        friendEntity.setGender(userFriendInfo.getGender());
        String url = loadImageUrl(userFriendInfo.getAvatar());
        friendEntity.setAvatar(url);
        return friendEntity;
    }

}
