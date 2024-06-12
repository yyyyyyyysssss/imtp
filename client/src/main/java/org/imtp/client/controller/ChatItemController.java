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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.imtp.client.entity.ChatItemEntity;

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
                ObservableList<Node> children = chatItemHBox.getChildren();
                children.clear();
                ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
                chatItemVBoxChildren.clear();
                if (!chatItemEntity.isSelf()){
                    chatItemHBox.setPadding(new Insets(0,0,0,0));
                    chatItemHBox.setAlignment(Pos.CENTER_LEFT);
                    sendNameLabel.setText(chatItemEntity.getName());
                    chatItemVBoxChildren.addAll(sendNameLabel,chatItemLabelHBox);
                    children.add(chatItemImageView);
                    children.add(chatItemVBox);
                    HBox.setMargin(chatItemVBox,new Insets(0,0,0,5));
                }else {
                    imageView.imageProperty().bind(chatItemEntity.imageProperty());
                    chatItemHBox.setPadding(new Insets(0,20,0,0));
                    chatItemHBox.setAlignment(Pos.CENTER_RIGHT);
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
