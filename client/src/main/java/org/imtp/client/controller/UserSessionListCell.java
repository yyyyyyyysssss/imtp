package org.imtp.client.controller;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.SessionEntity;
import org.imtp.client.util.FXMLLoadUtils;
import org.imtp.client.util.ResourceUtils;
import org.imtp.client.util.Tuple2;

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
        super.updateItem(sessionEntity, b);
        if (b || sessionEntity == null){
            setGraphic(null);
            setText(null);
        }else {
            String url = ResourceUtils.classPathResource("/img/tmp.jpg").toExternalForm();
            sessionEntity.setAvatar(url);
            sessionEntity.setLastMsg("好的吧");
            controller.initData(sessionEntity);
            setGraphic(hBox);
        }
    }
}
