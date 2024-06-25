package org.imtp.client.controller;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.util.TextUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.common.enums.DeliveryMethod;

import java.util.ArrayList;
import java.util.List;

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
    private TextFlow chatItemTextFlow;

    private ImageView imageView;

    private static final Insets FIVE_BOTTOM = new Insets(0,0,5,0);

    private static final Insets LEFT_INSETS = new Insets(0,0,0,10);

    private static final Insets RIGHT_INSETS = new Insets(0,10,0,0);

    private static final Insets RIGHT_INSETS_PADDING = new Insets(0,20,0,0);

    private static final CornerRadii CORNER_RADII = new CornerRadii(5);

    private static final Background BACKGROUND_LEFT = new Background(new BackgroundFill(Color.WHITE,CORNER_RADII,Insets.EMPTY));

    private static final Background BACKGROUND_RIGHT = new Background(new BackgroundFill(Color.LIGHTGREEN,CORNER_RADII,Insets.EMPTY));

    private final double max_width_image = 200;

    private final double max_height_image = 200;

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
                //清除节点
                ObservableList<Node> rootChildren = chatItemHBox.getChildren();
                ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
                ObservableList<Node> chatItemLabelHBoxChildren = chatItemLabelHBox.getChildren();
                ObservableList<Node> textFlowChildren = chatItemTextFlow.getChildren();
                textFlowChildren.clear();
                rootChildren.clear();
                chatItemVBoxChildren.clear();
                chatItemLabelHBoxChildren.clear();
                //组件节点
                if (!chatItemEntity.isSelf()){
                    leftNode(chatItemEntity);
                }else {
                    rightNode(chatItemEntity);
                }
                //展示的消息
                showMsg(chatItemEntity);
            });
        }
    }

    private void leftNode(ChatItemEntity chatItemEntity){
        ObservableList<Node> rootChildren = chatItemHBox.getChildren();
        ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
        ObservableList<Node> chatItemLabelHBoxChildren = chatItemLabelHBox.getChildren();

        chatItemTextFlow.setBackground(BACKGROUND_LEFT);
        chatItemHBox.setPadding(Insets.EMPTY);
        chatItemHBox.setAlignment(Pos.CENTER_LEFT);
        if (chatItemEntity.getDeliveryMethod().equals(DeliveryMethod.GROUP)){
            sendNameLabel.setText(chatItemEntity.getName());
            chatItemVBoxChildren.addAll(sendNameLabel,chatItemLabelHBox);
            VBox.setMargin(sendNameLabel,FIVE_BOTTOM);
        }else {
            chatItemVBoxChildren.add(chatItemLabelHBox);
        }
        chatItemLabelHBoxChildren.add(chatItemTextFlow);
        rootChildren.add(chatItemImageView);
        rootChildren.add(chatItemVBox);
        HBox.setMargin(chatItemVBox,LEFT_INSETS);
    }

    private void rightNode(ChatItemEntity chatItemEntity){
        ObservableList<Node> rootChildren = chatItemHBox.getChildren();
        ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
        ObservableList<Node> chatItemLabelHBoxChildren = chatItemLabelHBox.getChildren();

        imageView.imageProperty().bind(chatItemEntity.imageProperty());
        chatItemHBox.setPadding(RIGHT_INSETS_PADDING);
        chatItemHBox.setAlignment(Pos.CENTER_RIGHT);
        chatItemTextFlow.setBackground(BACKGROUND_RIGHT);
        chatItemLabelHBoxChildren.add(imageView);
        chatItemLabelHBoxChildren.add(chatItemTextFlow);
        chatItemVBoxChildren.add(chatItemLabelHBox);

        rootChildren.add(chatItemVBox);
        rootChildren.add(chatItemImageView);
        HBox.setMargin(chatItemVBox,RIGHT_INSETS);
    }

    private void showMsg(ChatItemEntity chatItemEntity){
        ObservableList<Node> textFlowChildren = chatItemTextFlow.getChildren();
        switch (chatItemEntity.getMessageType()){
            case TEXT_MESSAGE :
                List<Node> nodes = parseContent(chatItemEntity.getContent());
                textFlowChildren.addAll(nodes);
                break;
            case IMAGE_MESSAGE:
                chatItemTextFlow.setBackground(null);
                String path = chatItemEntity.getContent();
                ImageView iv = createImageView(path);
                textFlowChildren.add(iv);
                break;
        }
    }

    private List<Node> parseContent(String content){
        List<Node> nodes = new ArrayList<>();
        List<Object> objects = TextUtils.convertToStringAndEmojiObjects(content);
        for (Object obj : objects){
            if (obj instanceof String msg){
                Text text = createText(msg);
                nodes.add(text);
            }
            if (obj instanceof Emoji emoji){
                List<Node> nodeList = TextUtils.convertToTextAndImageNodes(emoji.character(),30);
                nodes.addAll(nodeList);
            }
        }
        return nodes;
    }

    private Text createText(String msg){
        Text text = new Text(msg);
        text.getStyleClass().add("text_flow_text");
        return text;
    }

    private ImageView createImageView(String path){
        Image image = new Image(path);
        double height = image.getHeight();
        double width = image.getWidth();
        ImageView iv = new ImageView(image);
        iv.setStyle("-fx-effect: dropshadow(three-pass-box, black, 5, 0, 0, 0);");
        if (height > max_height_image){
            iv.setFitHeight(max_height_image);
        }
        if (width > max_width_image){
            iv.setFitWidth(max_width_image);
        }
        return iv;
    }

    @Override
    public void update(Object object) {

    }
}
