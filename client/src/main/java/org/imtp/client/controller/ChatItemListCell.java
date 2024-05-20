package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.ChatItemEntity;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.util.FXMLLoadUtils;
import org.imtp.client.util.Tuple2;

@Slf4j
public class ChatItemListCell extends ListCell<ChatItemEntity> {

    private HBox hBox;

    private Controller controller;

    public ChatItemListCell(){
        Tuple2<Node, Controller> tuple2 = FXMLLoadUtils.loadFxmlAndControl(FXMLResourceConstant.CHAT_ITEM_FML);
        this.hBox = (HBox) tuple2.getV1();
        this.controller = tuple2.getV2();
    }

    @Override
    protected void updateItem(ChatItemEntity chatItemEntity, boolean b) {
        Platform.runLater(() -> {
            super.updateItem(chatItemEntity, b);
            if (b || chatItemEntity == null){
                setGraphic(null);
                setText(null);
            }else {
                controller.initData(chatItemEntity);
                setGraphic(hBox);
            }
        });
    }
}
