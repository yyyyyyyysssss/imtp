package org.imtp.client.model;

import org.imtp.common.packet.base.Packet;

public interface Observer {

    void update(Packet packet);

}
