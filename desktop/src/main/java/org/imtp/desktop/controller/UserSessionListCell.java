package org.imtp.desktop.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.constant.FXMLResourceConstant;
import org.imtp.desktop.entity.SessionEntity;
import org.imtp.desktop.util.FXMLLoadUtils;
import org.imtp.desktop.util.Tuple2;

@Slf4j
public class UserSessionListCell extends ListCell<SessionEntity> {

    private HBox hBox;

    private Controller controller;

    public UserSessionListCell(){
        Tuple2<Node, Controller> tuple2 = FXMLLoadUtils.loadFxmlAndControl(FXMLResourceConstant.USER_SESSION_ITEM_FML);
        this.hBox = (HBox) tuple2.getV1();
        this.controller = tuple2.getV2();
    }

    @Override
    protected void updateItem(SessionEntity sessionEntity, boolean b) {
        Platform.runLater(() -> {
            super.updateItem(sessionEntity, b);
            if (b || sessionEntity == null){
                setGraphic(null);
                setText(null);
            }else {
                controller.initData(sessionEntity);
                setGraphic(hBox);
            }
        });
    }
}
