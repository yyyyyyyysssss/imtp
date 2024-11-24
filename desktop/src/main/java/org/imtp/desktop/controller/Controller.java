package org.imtp.desktop.controller;

import org.imtp.desktop.SceneManager;
import org.imtp.desktop.constant.SendMessageListener;
import org.imtp.desktop.model.MessageModel;
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
