package org.imtp.client.controller;

import org.imtp.common.packet.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 16:51
 */
public interface Controller {

    void send(Packet packet);

}
