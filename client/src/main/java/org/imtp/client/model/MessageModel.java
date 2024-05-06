package org.imtp.client.model;


import org.imtp.common.packet.base.Packet;

public interface MessageModel {


    void setMessage(Packet packet);

    void sendMessage(Packet packet);

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Packet packet);

}
