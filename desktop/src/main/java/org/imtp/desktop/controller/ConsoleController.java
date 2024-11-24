package org.imtp.desktop.controller;

import org.imtp.desktop.SceneManager;
import org.imtp.desktop.constant.SendMessageListener;
import org.imtp.desktop.model.MessageModel;
import org.imtp.desktop.view.ConsoleView;
import org.imtp.common.packet.base.Packet;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 控制台发送消息
 * @Author ys
 * @Date 2024/4/23 16:52
 */
public class ConsoleController implements Controller {


    private ConsoleController(){
    }

    private ConsoleController(MessageModel messageModel){
        this.messageModel = messageModel;
        this.consoleView = new ConsoleView(this,messageModel);
        new Thread(consoleView).start();
    }

    private static volatile ConsoleController INSTANCE;

    private static final Lock lock = new ReentrantLock();

    private MessageModel messageModel;

    private ConsoleView consoleView;

    public static ConsoleController getInstance(MessageModel messageModel){
        if (INSTANCE == null){
            try {
                lock.lock();
                if (INSTANCE == null){
                    INSTANCE = new ConsoleController(messageModel);
                }
            }finally {
                lock.unlock();
            }
        }
        return INSTANCE;
    }

    @Override
    public void send(Packet packet) {
        messageModel.sendMessage(packet);
    }

    @Override
    public void send(Packet packet, SendMessageListener sendMessageListener) {
        messageModel.sendMessage(packet,sendMessageListener);
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {

    }

    @Override
    public void init(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    @Override
    public void initData(Object object) {

    }

    @Override
    public void updateData(Object object) {

    }
}
