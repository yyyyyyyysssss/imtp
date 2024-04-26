package org.imtp.client.controller;

import org.imtp.client.model.MessageModel;
import org.imtp.client.view.ConsoleView;
import org.imtp.common.packet.Packet;

/**
 * @Description 控制台发送消息
 * @Author ys
 * @Date 2024/4/23 16:52
 */
public class MessageController implements Controller {

    private MessageModel messageModel;

    private ConsoleView consoleView;

    public MessageController(MessageModel messageModel){
        this.messageModel = messageModel;
        this.consoleView = new ConsoleView(this,messageModel);
        new Thread(consoleView).start();
    }

    @Override
    public void send(Packet packet) {
        messageModel.sendMessage(packet);
    }
}
