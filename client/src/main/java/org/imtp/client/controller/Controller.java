package org.imtp.client.controller;

import org.imtp.client.enums.MessageType;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 16:51
 */
public interface Controller {

    void send(String msg, long receiver, MessageType messageType);

}
