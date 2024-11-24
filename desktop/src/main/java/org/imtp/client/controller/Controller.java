package org.imtp.client.controller;

import org.imtp.client.SceneManager;
import org.imtp.client.constant.SendMessageListener;
import org.imtp.client.model.MessageModel;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 16:51
 */
public interface Controller {

    void send(Packet packet);

    void send(Packet packet, SendMessageListener sendMessageListener);

    void setSceneManager(SceneManager sceneManager);

    void init(MessageModel messageModel);

    void initData(Object object);

    void updateData(Object object);

}
