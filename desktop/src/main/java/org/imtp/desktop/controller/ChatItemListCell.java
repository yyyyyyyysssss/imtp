package org.imtp.desktop.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.constant.FXMLResourceConstant;
import org.imtp.desktop.entity.ChatItemEntity;
import org.imtp.desktop.entity.SessionEntity;
import org.imtp.desktop.util.FXMLLoadUtils;
import org.imtp.desktop.util.Tuple2;

@Slf4j
public class ChatItemListCell extends ListCell<ChatItemEntity> {

    private HBox hBox;

    private ChatItemController controller;

    private SessionEntity sessionEntity;

    public ChatItemListCell(SessionEntity sessionEntity){
        this.sessionEntity = sessionEntity;
        Tuple2<Node, Controller> tuple2 = FXMLLoadUtils.loadFxmlAndControl(FXMLResourceConstant.CHAT_ITEM_FML);
        this.hBox = (HBox) tuple2.getV1();
        this.controller = (ChatItemController)tuple2.getV2();
        controller.setSessionEntity(sessionEntity);
        this.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null){
                controller.initData(newItem);
            }
        });
        this.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
            if (isEmpty){
                setGraphic(null);
            }else {
                setGraphic(hBox);
            }
        });
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(ChatItemEntity chatItemEntity, boolean b) {
        super.updateItem(chatItemEntity, b);
        Platform.runLater(() -> {
            if (b || chatItemEntity == null){
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(hBox);
            }
        });
    }
}
