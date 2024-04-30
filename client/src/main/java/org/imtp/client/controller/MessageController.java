package org.imtp.client.controller;

import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.enums.MessageType;
import org.imtp.client.model.MessageModel;
import org.imtp.client.view.ConsoleView;
import org.imtp.common.packet.GroupChatMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.PrivateChatMessage;

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
    public void send(String msg, long receiver, MessageType messageType) {
        Packet packet;
        switch (messageType){
            case PRIVATE :
                packet = new PrivateChatMessage(msg, ClientContextHolder.clientContext().id(),receiver);
                break;
            case GROUP:
                packet = new GroupChatMessage(msg, ClientContextHolder.clientContext().id(),receiver);
                break;
            default:
                throw new UnsupportedOperationException("未知的操作类型");
        }
        messageModel.sendMessage(packet);
    }
}
