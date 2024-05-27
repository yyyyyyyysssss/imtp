package org.imtp.client.controller;

import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.SceneManager;
import org.imtp.client.model.MessageModel;
import org.imtp.client.model.Observer;
import org.imtp.client.util.FXMLLoadUtils;
import org.imtp.client.util.Tuple2;
import org.imtp.common.packet.base.Packet;

@Slf4j
public abstract class AbstractController implements Controller, Observer {

    protected MessageModel messageModel;

    protected SceneManager sceneManager;

    @Override
    public void initData(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateData(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(MessageModel messageModel) {
        this.messageModel = messageModel;
        messageModel.registerObserver(this);
        init0();
    }

    protected abstract void init0();

    @Override
    public void send(Packet packet) {
        this.messageModel.sendMessage(packet);
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    protected void skipScene(String fxmlPath,String title,MessageModel messageModel){
        this.sceneManager.setScene(fxmlPath,title,messageModel);
    }

    protected Tuple2<Node, Controller> loadNodeAndController(String fxmlPath){
        Tuple2<Node, Controller> tuple2 = FXMLLoadUtils.loadFxmlAndControl(fxmlPath);
        Controller controller = tuple2.getV2();
        controller.init(messageModel);
        return tuple2;
    }

    //根据节点获取最近的控制器
    protected Controller getController(Node node){
        Controller controller = null;
        do {
            controller = (Controller) node.getUserData();
            node = node.getParent();
        }while (controller == null && node != null);
        return controller;
    }

}
