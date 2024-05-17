package org.imtp.client.model;


import org.imtp.common.packet.base.Packet;

public interface MessageModel extends Model{

    void pullFriendship();

    void pullGroupRelationship();

    void pullOfflineMessage();

    void pullUserSession();

    void publishMessage(Packet packet);

    void sendMessage(Packet packet);

    MessageModel getNextModel();

}
