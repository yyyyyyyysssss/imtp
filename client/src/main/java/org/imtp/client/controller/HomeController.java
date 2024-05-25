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
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.Tuple2;
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
    private ImageView homeChatImageView;

    private Image chatIconImage;

    private Image chatIconSelectedImage;

    @FXML
    private ImageView homeFriendImageView;

    private Image friendIconImage;

    private Image friendIconSelectedImage;

    private ImageUrlParse imageUrlParse;

    @FXML
    private Pane homePane;

    private Node chatNode;

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
        this.chatIconImage = new Image(chatIconUrl.toExternalForm());
        URL chatIconSelectedUrl = ResourceUtils.classPathResource("/img/home_chat_icon_selected.png");
        this.chatIconSelectedImage = new Image(chatIconSelectedUrl.toExternalForm());
        //初始化好友图标
        URL friendIconurl = ResourceUtils.classPathResource("/img/home_friend_icon.png");
        this.friendIconImage = new Image(friendIconurl.toExternalForm());
        URL friendIconSelectedurl = ResourceUtils.classPathResource("/img/home_friend_icon_selected.png");
        this.friendIconSelectedImage = new Image(friendIconSelectedurl.toExternalForm());
        //设置默认图标
        homeChatImageView.setImage(chatIconImage);
        homeFriendImageView.setImage(friendIconImage);


        homeChatImageView.setOnMouseClicked(mouseEvent -> {
            homeChatImageView.setImage(chatIconSelectedImage);
            homeFriendImageView.setImage(friendIconImage);

            ObservableList<Node> children = homePane.getChildren();
            if (!children.isEmpty()){
                children.removeLast();
            }
            children.add(chatNode);
        });

        homeFriendImageView.setOnMouseClicked(mouseEvent -> {
            homeChatImageView.setImage(chatIconImage);
            homeFriendImageView.setImage(friendIconSelectedImage);

            ObservableList<Node> children = homePane.getChildren();
            if (!children.isEmpty()){
                children.removeLast();
            }
            //TODO

        });

    }

    @Override
    protected void init0() {
        Tuple2<Node, Controller> tuple2 = loadNodeAndController(FXMLResourceConstant.USER_SESSION_FML);
        this.chatNode = tuple2.getV1();
        ObservableList<Node> children = homePane.getChildren();
        children.add(chatNode);
    }

    @Override
    public void update(Object object) {

    }
}
