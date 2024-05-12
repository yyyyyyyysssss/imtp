package org.imtp.client.controller;

import org.imtp.client.SceneManager;
import org.imtp.client.model.MessageModel;
import org.imtp.client.model.Observer;
import org.imtp.common.packet.base.Packet;

public abstract class AbstractController implements Controller, Observer {

    protected MessageModel messageModel;

    protected SceneManager sceneManager;

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

    protected void switchScene(String fxmlPath,String title,MessageModel messageModel){
        this.sceneManager.setScene(fxmlPath,title,messageModel);
    }
}
