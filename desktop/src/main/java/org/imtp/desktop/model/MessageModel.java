package org.imtp.desktop.model;


import org.imtp.desktop.constant.SendMessageListener;
import org.imtp.common.packet.base.Packet;

public interface MessageModel extends Model{

    void publishMessage(Packet packet);

    void sendMessage(Packet packet);

    void sendMessage(Packet packet, SendMessageListener sendMessageListener);

}
