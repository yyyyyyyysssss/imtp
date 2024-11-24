package org.imtp.desktop.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.constant.Callback;
import org.imtp.desktop.constant.FXMLResourceConstant;
import org.imtp.desktop.entity.GroupEntity;
import org.imtp.desktop.util.Tuple2;
import org.imtp.common.packet.GroupRelationshipResponse;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserGroupController extends AbstractController implements Callback<Long> {

    @FXML
    private HBox userGroupHBox;

    @FXML
    private ListView<GroupEntity> groupListView;

    @FXML
    private BorderPane groupPane;

    private HomeController homeController;

    private UserSessionController userSessionController;

    private Map<Long,Node> userGroupNodeMap;

    //用户群组
    private Map<Long, UserGroupInfo> userGroupInfoMap;

    //群组关联带用户
    private Map<String, UserFriendInfo> groupUserInfoMap;

    private final String groupUserSeparator = "-";

    @FXML
    public void initialize(){
        userGroupNodeMap = new HashMap<>();
        userGroupInfoMap = new HashMap<>();
        groupUserInfoMap = new HashMap<>();

        groupListView.setCellFactory(c -> new UserGroupListCell());
        groupListView.setFocusTraversable(false);
        groupListView.setOnMouseClicked(mouseEvent -> {
            GroupEntity groupEntity = groupListView.getSelectionModel().getSelectedItem();
            if (groupEntity == null){
                return;
            }
            Node node;
            if (( node = userGroupNodeMap.get(groupEntity.getId())) == null){
                node = addGroupDetailsNode(groupEntity);
            }

            groupPane.setCenter(node);
        });

        groupListView.prefHeightProperty().bind(userGroupHBox.heightProperty());
        groupPane.prefHeightProperty().bind(userGroupHBox.heightProperty());

        userGroupHBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                groupPane.setPrefWidth(t1.doubleValue() - groupListView.getPrefWidth());
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
        //拉取用户群组关系
        messageModel.pullGroupRelationship();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case GROUP_RELATIONSHIP_RES:
                GroupRelationshipResponse groupRelationshipResponse = (GroupRelationshipResponse) packet;
                List<UserGroupInfo> userGroupInfos = groupRelationshipResponse.getUserGroupInfos();
                if (userGroupInfos != null && !userGroupInfos.isEmpty()){
                    for (UserGroupInfo userGroupInfo : userGroupInfos){
                        setListView(convertFriendEntity(userGroupInfo));
                        userGroupInfoMap.put(userGroupInfo.getId(), userGroupInfo);

                        List<UserFriendInfo> groupUserInfos = userGroupInfo.getGroupUserInfos();
                        for (UserFriendInfo groupUserInfo : groupUserInfos){
                            String key = userGroupInfo.getId() + groupUserSeparator + groupUserInfo.getId();
                            groupUserInfoMap.put(key,groupUserInfo);
                        }
                    }
                }
                break;
        }
    }

    private void setListView(List<GroupEntity> groupEntities){
        for (GroupEntity groupEntity : groupEntities){
            setListView(groupEntity);
        }
    }

    private void setListView(GroupEntity groupEntity){
        addUserGroupNode(groupEntity);
    }

    private void addUserGroupNode(GroupEntity groupEntity){
        addUserGroupNode(groupEntity,false);
    }

    private void addUserGroupNode(GroupEntity groupEntity, boolean selected){
        groupListView.getItems().addFirst(groupEntity);
        if (selected){
            groupListView.getSelectionModel().select(groupEntity);
        }
    }

    public UserGroupInfo findUserGroupInfo(Long groupId){

        return userGroupInfoMap.get(groupId);
    }

    public UserFriendInfo findGroupUserInfo(Long groupId, Long userId){
        String key = groupId + groupUserSeparator + userId;
        return groupUserInfoMap.get(key);
    }

    //添加好友关联的详情
    private Node addGroupDetailsNode(GroupEntity groupEntity){
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.USER_GROUP_DETAILS_FML);
        UserGroupDetailsController controller = (UserGroupDetailsController)tuple2.getV2();
        controller.initData(groupEntity);
        controller.setCallback(this);
        Node node = tuple2.getV1();
        userGroupNodeMap.put(groupEntity.getId(), node);
        return node;
    }

    @Override
    public void callback(Long userId) {
        UserGroupInfo userGroupInfo = userGroupInfoMap.get(userId);
        homeController.switchUserSession();
        userSessionController.addUserSessionAndChatNode(userGroupInfo);
    }

    private GroupEntity convertFriendEntity(UserGroupInfo userGroupInfo){
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setId(userGroupInfo.getId());
        groupEntity.setName(userGroupInfo.getGroupName());
        String url = loadImageUrl(userGroupInfo.getAvatar());
        groupEntity.setAvatar(url);
        return groupEntity;
    }

}
