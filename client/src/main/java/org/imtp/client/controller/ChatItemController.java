package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.common.enums.DeliveryMethod;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/20 16:34
 */
public class ChatItemController extends AbstractController{

    @FXML
    private HBox chatItemHBox;

    @FXML
    private HBox chatItemLabelHBox;

    @FXML
    private VBox chatItemVBox;

    @FXML
    private Label sendNameLabel;

    @FXML
    private ImageView chatItemImageView;

    @FXML
    private Label chatItemLabel;

    private ImageView imageView;


    @FXML
    public void initialize(){
        imageView = new ImageView();
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
    }


    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if (object instanceof ChatItemEntity chatItemEntity){
            Platform.runLater(() -> {
                chatItemImageView.setImage(new Image(chatItemEntity.getAvatar()));
                chatItemLabel.setText(chatItemEntity.getContent());
                chatItemLabel.setPadding(new Insets(7,7,7,7));
                ObservableList<Node> children = chatItemHBox.getChildren();
                children.clear();
                ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
                chatItemVBoxChildren.clear();
                CornerRadii cornerRadii = new CornerRadii(5);
                if (!chatItemEntity.isSelf()){
                    chatItemLabel.setBackground(new Background(new BackgroundFill(Color.WHITE,cornerRadii,Insets.EMPTY)));
                    chatItemHBox.setPadding(new Insets(0,0,0,0));
                    chatItemHBox.setAlignment(Pos.CENTER_LEFT);
                    if (chatItemEntity.getDeliveryMethod().equals(DeliveryMethod.GROUP)){
                        sendNameLabel.setText(chatItemEntity.getName());
                        chatItemVBoxChildren.addAll(sendNameLabel,chatItemLabelHBox);
                    }else {
                        chatItemVBoxChildren.add(chatItemLabelHBox);
                    }
                    children.add(chatItemImageView);
                    children.add(chatItemVBox);
                    HBox.setMargin(chatItemVBox,new Insets(0,0,0,5));
                }else {
                    imageView.imageProperty().bind(chatItemEntity.imageProperty());
                    chatItemHBox.setPadding(new Insets(0,20,0,0));
                    chatItemHBox.setAlignment(Pos.CENTER_RIGHT);
                    chatItemLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,cornerRadii,Insets.EMPTY)));
                    ObservableList<Node> cd = chatItemLabelHBox.getChildren();
                    cd.clear();
                    cd.add(imageView);
                    cd.add(chatItemLabel);
                    chatItemVBoxChildren.add(chatItemLabelHBox);
                    children.add(chatItemVBox);
                    children.add(chatItemImageView);
                    HBox.setMargin(chatItemVBox,new Insets(0,10,0,0));
                }
            });
        }
    }

    @Override
    public void update(Object object) {

    }
}
