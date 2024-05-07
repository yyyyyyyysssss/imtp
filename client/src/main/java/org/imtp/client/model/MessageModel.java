package org.imtp.client.model;


import org.imtp.common.packet.base.Packet;

public interface MessageModel extends Model{

    void publishMessage(Packet packet);

    void sendMessage(Packet packet);

}
