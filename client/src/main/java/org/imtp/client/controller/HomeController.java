package org.imtp.client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.component.ClassPathImageUrlParse;
import org.imtp.client.component.ImageUrlParse;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.context.DefaultClientUserChannelContext;
import org.imtp.client.event.UserFriendEvent;
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.Tuple2;
import org.imtp.common.packet.body.UserFriendInfo;
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
    private VBox homeVbox;

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

    private ImageUrlParse imageUrlParse;

    @FXML
    private Pane homePane;

    private Node sessionNode;

    private UserSessionController sessionController;

    private Node friendNode;

    private UserFriendController friendController;

    @FXML
    public void initialize(){
        this.imageUrlParse = new ClassPathImageUrlParse();
        //设置当前登录人头像
        DefaultClientUserChannelContext userChannelContext = (DefaultClientUserChannelContext)ClientContextHolder.clientContext();
        UserInfo userInfo = userChannelContext.getUserInfo();
        String avatar = userInfo.getAvatar();
        String avatarUrl = imageUrlParse.loadUrl(avatar);
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
        //设置默认图标
        homeSessionImageView.setImage(sessionIconSelectedImage);
        homeFriendImageView.setImage(friendIconImage);


        homeSessionImageView.setOnMouseClicked(mouseEvent -> {
            switchUserSession();
        });

        homeFriendImageView.setOnMouseClicked(mouseEvent -> {
            switchUserFriend();
        });

    }

    @Override
    protected void init0() {
        Tuple2<Node, Controller> chatTuple2 = loadNodeAndController(FXMLResourceConstant.USER_SESSION_FML);
        this.sessionNode = chatTuple2.getV1();
        this.sessionController = (UserSessionController) chatTuple2.getV2();

        Tuple2<Node, Controller> friendTuple2 = loadNodeAndController(FXMLResourceConstant.USER_FRIEND_FML);
        this.friendNode = friendTuple2.getV1();
        this.friendController = (UserFriendController) friendTuple2.getV2();

        //相互引用对方
        friendController.setUserSessionController(sessionController);
        friendController.setHomeController(this);
        sessionController.setUserFriendController(friendController);
        sessionController.setHomeController(this);

        //设置默认组件
        ObservableList<Node> children = homePane.getChildren();
        children.add(sessionNode);
    }

    @Override
    public void update(Object object) {

    }

    public void switchUserSession(){
        homeSessionImageView.setImage(sessionIconSelectedImage);
        homeFriendImageView.setImage(friendIconImage);

        ObservableList<Node> children = homePane.getChildren();
        if (!children.isEmpty()){
            children.removeLast();
        }
        children.add(sessionNode);
    }

    public void switchUserFriend(){
        homeSessionImageView.setImage(sessionIconImage);
        homeFriendImageView.setImage(friendIconSelectedImage);

        ObservableList<Node> children = homePane.getChildren();
        if (!children.isEmpty()){
            children.removeLast();
        }
        children.add(friendNode);
    }

}
