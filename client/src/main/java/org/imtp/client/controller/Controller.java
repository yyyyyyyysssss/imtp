package org.imtp.client.controller;

import org.imtp.client.SceneManager;
import org.imtp.client.model.MessageModel;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/23 16:51
 */
public interface Controller {

    void send(Packet packet);

    void setSceneManager(SceneManager sceneManager);

    void init(MessageModel messageModel);

}
