package org.imtp.desktop.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.SceneManager;
import org.imtp.desktop.SceneManagerHolder;
import org.imtp.desktop.constant.FXMLResourceConstant;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.context.DefaultClientUserChannelContext;
import org.imtp.desktop.util.ResizeHelper;
import org.imtp.desktop.util.ResourceUtils;
import org.imtp.desktop.util.Tuple2;
import org.imtp.common.packet.body.UserInfo;

import java.net.URL;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/14 16:27
 */
@Slf4j
public class HomeController extends AbstractController{

    @FXML
    private BorderPane rootPane;

    @FXML
    private BorderPane centerBorderPane;

    @FXML
    private HBox centerBorderPaneHBox;

    @FXML
    private HBox centerHomeLeftHBox;

    @FXML
    private HBox centerHomeRightHBox;

    @FXML
    private Label headName;

    @FXML
    private ImageView homeAvatarImageView;

    @FXML
    private ImageView homeSessionImageView;

    private Image sessionIconImage;

    private Image sessionIconSelectedImage;

    @FXML
    private ImageView homeFriendImageView;

    private Image friendIconImage;

    private Image friendIconSelectedImage;

    @FXML
    private ImageView homeGroupImageView;

    @FXML
    private TextField homeSearch;

    private Image groupIconImage;

    private Image groupIconSelectedImage;

    private Node sessionNode;

    private UserSessionController sessionController;

    private Node friendNode;

    private UserFriendController friendController;

    private Node groupNode;

    private UserGroupController groupController;

    private final static double MIN_HEIGHT = 750.0;

    private final static double MIN_WIDTH = 1000.0;

    @FXML
    private BorderPane centerHomeRightBorderPane;

    @FXML
    public void initialize(){
        //设置当前登录人头像
        DefaultClientUserChannelContext userChannelContext = (DefaultClientUserChannelContext)ClientContextHolder.clientContext();
        UserInfo userInfo = userChannelContext.getUserInfo();
        String avatar = userInfo.getAvatar();
        String avatarUrl = loadImageUrl(avatar);
        homeAvatarImageView.setImage(new Image(avatarUrl));
        //初始化聊天图标
        URL chatIconUrl = ResourceUtils.classPathResource("/img/home_chat_icon.png");
        this.sessionIconImage = new Image(chatIconUrl.toExternalForm());
        URL chatIconSelectedUrl = ResourceUtils.classPathResource("/img/home_chat_icon_selected.png");
        this.sessionIconSelectedImage = new Image(chatIconSelectedUrl.toExternalForm());
        //初始化好友图标
        URL friendIconurl = ResourceUtils.classPathResource("/img/home_friend_icon.png");
        this.friendIconImage = new Image(friendIconurl.toExternalForm());
        URL friendIconSelectedurl = ResourceUtils.classPathResource("/img/home_friend_icon_selected.png");
        this.friendIconSelectedImage = new Image(friendIconSelectedurl.toExternalForm());
        //初始化好友图标
        URL groupIconurl = ResourceUtils.classPathResource("/img/home_group_icon.png");
        this.groupIconImage = new Image(groupIconurl.toExternalForm());
        URL groupIconSelectedurl = ResourceUtils.classPathResource("/img/home_group_icon_selected.png");
        this.groupIconSelectedImage = new Image(groupIconSelectedurl.toExternalForm());
        //设置默认图标
        homeSessionImageView.setImage(sessionIconSelectedImage);
        homeFriendImageView.setImage(friendIconImage);
        homeGroupImageView.setImage(groupIconImage);


        homeSessionImageView.setOnMouseClicked(mouseEvent -> {
            switchUserSession();
        });

        homeFriendImageView.setOnMouseClicked(mouseEvent -> {
            switchUserFriend();
        });

        homeGroupImageView.setOnMouseClicked(mouseEvent -> {
            switchUserGroup();
        });

        centerBorderPaneHBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double prefWidth = centerHomeLeftHBox.getPrefWidth();
                centerHomeRightBorderPane.setPrefWidth(t1.doubleValue() - prefWidth);
            }
        });

        homeSearch.setFocusTraversable(false);

        SceneManager sceneManager = SceneManagerHolder.getSceneManager();
        Stage stage = sceneManager.getStage();
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.heightProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() < MIN_HEIGHT){
                stage.setHeight(MIN_HEIGHT);
            }
        });
        stage.widthProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() < MIN_WIDTH){
                stage.setMinWidth(MIN_WIDTH);
            }
        });
    }

    @Override
    protected void init0() {

        //设置拖动以及窗口伸缩
        SceneManager sceneManager = SceneManagerHolder.getSceneManager();
        Stage stage = sceneManager.getStage();
        ResizeHelper.addResizeListener(stage);

        Tuple2<Node, Controller> chatTuple2 = loadNodeAndController(FXMLResourceConstant.USER_SESSION_FML);
        this.sessionNode = chatTuple2.getV1();
        this.sessionController = (UserSessionController) chatTuple2.getV2();

        Tuple2<Node, Controller> friendTuple2 = loadNodeAndController(FXMLResourceConstant.USER_FRIEND_FML);
        this.friendNode = friendTuple2.getV1();
        this.friendController = (UserFriendController) friendTuple2.getV2();

        Tuple2<Node, Controller> groupTuple2 = loadNodeAndController(FXMLResourceConstant.USER_GROUP_FML);
        this.groupNode = groupTuple2.getV1();
        this.groupController = (UserGroupController) groupTuple2.getV2();

        //引用
        sessionController.setUserFriendController(friendController);
        sessionController.setUserGroupController(groupController);
        sessionController.setHomeController(this);

        friendController.setUserSessionController(sessionController);
        friendController.setHomeController(this);

        groupController.setUserSessionController(sessionController);
        groupController.setHomeController(this);

        //设置默认组件
        centerBorderPane.setCenter(sessionNode);

    }

    @Override
    public void update(Object object) {

    }

    public void switchUserSession(){
        homeSessionImageView.setImage(sessionIconSelectedImage);
        homeFriendImageView.setImage(friendIconImage);
        homeGroupImageView.setImage(groupIconImage);

        centerBorderPane.setCenter(sessionNode);

        headName.setVisible(true);
    }

    public void switchUserFriend(){
        homeFriendImageView.setImage(friendIconSelectedImage);
        homeSessionImageView.setImage(sessionIconImage);
        homeGroupImageView.setImage(groupIconImage);

        centerBorderPane.setCenter(friendNode);

        headName.setVisible(false);
    }

    public void switchUserGroup(){
        homeGroupImageView.setImage(groupIconSelectedImage);
        homeSessionImageView.setImage(sessionIconImage);
        homeFriendImageView.setImage(friendIconImage);

        centerBorderPane.setCenter(groupNode);

        headName.setVisible(false);
    }

    public void setHeadName(String content){
        headName.setText(content);
    }

}
