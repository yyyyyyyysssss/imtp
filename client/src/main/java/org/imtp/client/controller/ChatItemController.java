package org.imtp.client.controller;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.util.TextUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.MessageMetadata;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/20 16:34
 */
@Slf4j
public class ChatItemController extends AbstractController {

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

    private Image videoPlayerIcon;

    private static final Insets FIVE_BOTTOM = new Insets(0, 0, 5, 0);

    private static final Insets LEFT_INSETS = new Insets(0, 0, 0, 10);

    private static final Insets RIGHT_INSETS = new Insets(0, 10, 0, 0);

    private static final Insets RIGHT_INSETS_PADDING = Insets.EMPTY;

    private static final CornerRadii CORNER_RADII = new CornerRadii(5);

    private static final Background BACKGROUND_LEFT = new Background(new BackgroundFill(Color.WHITE, CORNER_RADII, Insets.EMPTY));

    private static final Background BACKGROUND_RIGHT = new Background(new BackgroundFill(Color.LIGHTGREEN, CORNER_RADII, Insets.EMPTY));

    private static final Paint GRADIENT_PAINT = new LinearGradient(
            0, 1, // 渐变起始点（x, y）
            0, 0, // 渐变结束点（x, y）
            true, // 是否重复
            CycleMethod.NO_CYCLE, // 循环方式
            new Stop(0, new Color(0, 0, 0, 0.5)), // 开始颜色
            new Stop(1, new Color(0, 0, 0, 0)) // 结束颜色
    );

    private final double max_width_image = 200;

    private final double max_width_video = 120;

    @FXML
    public void initialize() {
        imageView = new ImageView();
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);

        URL videoPlayerIconUrl = ResourceUtils.classPathResource("/img/video_play_48.png");
        videoPlayerIcon = new Image(videoPlayerIconUrl.toExternalForm());

        chatItemHBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double maxWidth = chatItemTextFlow.getMaxWidth();
                double d = t1.doubleValue() / (number.doubleValue() == 0.0 ? t1.doubleValue() : number.doubleValue());
                chatItemTextFlow.setMaxWidth(maxWidth * d);
            }
        });

    }


    @Override
    protected void init0() {

    }

    @Override
    public void initData(Object object) {
        if (object instanceof ChatItemEntity chatItemEntity) {
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
                if (!chatItemEntity.isSelf()) {
                    leftNode(chatItemEntity);
                } else {
                    rightNode(chatItemEntity);
                }
                //展示的消息
                showMsg(chatItemEntity);
            });
        }
    }

    private void leftNode(ChatItemEntity chatItemEntity) {
        ObservableList<Node> rootChildren = chatItemHBox.getChildren();
        ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
        ObservableList<Node> chatItemLabelHBoxChildren = chatItemLabelHBox.getChildren();

        chatItemTextFlow.setBackground(BACKGROUND_LEFT);
        chatItemHBox.setPadding(Insets.EMPTY);
        chatItemHBox.setAlignment(Pos.TOP_LEFT);
        if (chatItemEntity.getDeliveryMethod().equals(DeliveryMethod.GROUP)) {
            sendNameLabel.setText(chatItemEntity.getName());
            chatItemVBoxChildren.addAll(sendNameLabel, chatItemLabelHBox);
            VBox.setMargin(sendNameLabel, FIVE_BOTTOM);
        } else {
            chatItemVBoxChildren.add(chatItemLabelHBox);
        }
        chatItemLabelHBoxChildren.add(chatItemTextFlow);
        rootChildren.add(chatItemImageView);
        rootChildren.add(chatItemVBox);
        HBox.setMargin(chatItemVBox, LEFT_INSETS);
    }

    private void rightNode(ChatItemEntity chatItemEntity) {
        ObservableList<Node> rootChildren = chatItemHBox.getChildren();
        ObservableList<Node> chatItemVBoxChildren = chatItemVBox.getChildren();
        ObservableList<Node> chatItemLabelHBoxChildren = chatItemLabelHBox.getChildren();

        imageView.imageProperty().bind(chatItemEntity.imageProperty());
        chatItemHBox.setPadding(RIGHT_INSETS_PADDING);
        chatItemHBox.setAlignment(Pos.TOP_RIGHT);
        chatItemTextFlow.setBackground(BACKGROUND_RIGHT);
        chatItemLabelHBoxChildren.add(imageView);
        chatItemLabelHBoxChildren.add(chatItemTextFlow);
        chatItemVBoxChildren.add(chatItemLabelHBox);

        rootChildren.add(chatItemVBox);
        rootChildren.add(chatItemImageView);
        HBox.setMargin(chatItemVBox, RIGHT_INSETS);
    }

    private void showMsg(ChatItemEntity chatItemEntity) {
        ObservableList<Node> textFlowChildren = chatItemTextFlow.getChildren();
        switch (chatItemEntity.getMessageType()) {
            case TEXT_MESSAGE:
                List<Node> nodes = parseContent(chatItemEntity.getContent());
                textFlowChildren.addAll(nodes);
                break;
            case IMAGE_MESSAGE:
                chatItemTextFlow.setBackground(null);
                String path = chatItemEntity.getContent();
                MessageMetadata imageMessageMetadata = chatItemEntity.getMessageMetadata();
                ImageView imageIV = createImageMessageView(path, imageMessageMetadata);
                textFlowChildren.add(imageIV);
                break;
            case VIDEO_MESSAGE:
                chatItemTextFlow.setBackground(null);
                MessageMetadata videoMessageMetadata = chatItemEntity.getMessageMetadata();
                Node videoNode = createVideoMessageView(chatItemEntity.getContent(), videoMessageMetadata);
                textFlowChildren.add(videoNode);
                break;
        }
    }

    private List<Node> parseContent(String content) {
        List<Node> nodes = new ArrayList<>();
        List<Object> objects = TextUtils.convertToStringAndEmojiObjects(content);
        for (Object obj : objects) {
            if (obj instanceof String msg) {
                Text text = createText(msg);
                nodes.add(text);
            }
            if (obj instanceof Emoji emoji) {
                List<Node> nodeList = TextUtils.convertToTextAndImageNodes(emoji.character(), 30);
                nodes.addAll(nodeList);
            }
        }
        return nodes;
    }

    private Text createText(String msg) {
        Text text = new Text(msg);
        text.getStyleClass().add("text_flow_text");
        return text;
    }

    private ImageView createImageMessageView(String url, MessageMetadata messageMetadata) {
        double height = messageMetadata.getHeight();
        double width = messageMetadata.getWidth();
        ImageView iv = new ImageView(url);
        iv.setStyle("-fx-effect: dropshadow(three-pass-box, lightgray, 1, 0.5, 0, 0);");
        if (width > max_width_image) {
            iv.setFitWidth(max_width_image);
            double h = max_width_image * height / width;
            iv.setFitHeight(h);
        }
        return iv;
    }

    private Node createVideoMessageView(String url, MessageMetadata messageMetadata) {
        double height = messageMetadata.getHeight();
        double width = messageMetadata.getWidth();
        ImageView iv = new ImageView(messageMetadata.getThumbnailUrl());
        iv.setPreserveRatio(true);
        iv.setStyle("-fx-effect: dropshadow(three-pass-box, lightgray, 1, 0.5, 0, 0);");
        if (width > max_width_video) {
            iv.setFitWidth(max_width_video);
            double h = max_width_video * height / width;
            iv.setFitHeight(h);
        }

        //创建渐变矩形
        Rectangle gradientRect = new Rectangle(0, 0, iv.getFitWidth(), 50);
        gradientRect.setFill(GRADIENT_PAINT);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(iv, new ImageView(videoPlayerIcon));

        Label label = new Label(messageMetadata.getDurationDesc());
        label.setStyle("-fx-text-fill: white");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(stackPane, gradientRect, label);

        //时间放在右下角
        AnchorPane.setRightAnchor(label, 10.0);
        AnchorPane.setBottomAnchor(label, 10.0);

        AnchorPane.setBottomAnchor(gradientRect, 0.0);

        anchorPane.setOnMouseClicked(m -> {
            log.info("video player");
            VideoPlayerDialog videoPlayerDialog = VideoPlayerDialog.getInstance();
            videoPlayerDialog.showPane(url,messageMetadata.getWidth(),messageMetadata.getHeight());
        });

        return anchorPane;
    }

    @Override
    public void update(Object object) {

    }
}
