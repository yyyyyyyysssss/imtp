package org.imtp.client.controller;

import org.imtp.client.SceneManager;
import org.imtp.client.model.MessageModel;
import org.imtp.client.view.ConsoleView;
import org.imtp.common.packet.base.Packet;

/**
 * @Description 控制台发送消息
 * @Author ys
 * @Date 2024/4/23 16:52
 */
public class ConsoleController implements Controller {

    private MessageModel messageModel;

    private ConsoleView consoleView;

    public ConsoleController(MessageModel messageModel){
        this.messageModel = messageModel;
        this.consoleView = new ConsoleView(this,messageModel);
        new Thread(consoleView).start();
    }

    @Override
    public void send(Packet packet) {
        messageModel.sendMessage(packet);
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {

    }

    @Override
    public void init(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void initData(Object object) {

    }
}
