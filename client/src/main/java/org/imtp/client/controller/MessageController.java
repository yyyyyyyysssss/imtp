package org.imtp.client.controller;

import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.model.MessageModel;
import org.imtp.client.view.ConsoleView;
import org.imtp.common.enums.DeliveryMethod;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;

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
