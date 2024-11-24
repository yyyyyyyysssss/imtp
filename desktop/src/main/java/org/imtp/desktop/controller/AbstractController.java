package org.imtp.desktop.controller;

import io.netty.channel.EventLoop;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import org.imtp.desktop.SceneManager;
import org.imtp.desktop.component.ClassPathImageUrlParse;
import org.imtp.desktop.component.ImageUrlParse;
import org.imtp.desktop.constant.SendMessageListener;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.model.MessageModel;
import org.imtp.desktop.model.Observer;
import org.imtp.desktop.util.FXMLLoadUtils;
import org.imtp.desktop.util.Tuple2;
import org.imtp.common.packet.base.Packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class AbstractController implements Controller, Observer {

    protected MessageModel messageModel;

    protected SceneManager sceneManager;

    private ImageUrlParse imageUrlParse;

    private final Lock lock = new ReentrantLock();

    private Map<Packet, RetryTask> retryTaskMap;

    @Override
    public void initData(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateData(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(MessageModel messageModel) {
        this.messageModel = messageModel;
        messageModel.registerObserver(this);
        retryTaskMap = new ConcurrentHashMap<>();
        init0();
    }

    protected abstract void init0();

    @Override
    public void send(Packet packet) {
        RetryTask retryTask = new RetryTask(3);
        retryTaskMap.put(packet,retryTask);
        send(packet, new SendMessageListener() {
            @Override
            public void isSuccess() {
                RetryTask rt = retryTaskMap.get(packet);
                if (rt != null){
                    rt.cancel();
                    retryTaskMap.remove(packet);
                }
            }
            @Override
            public void isFail() {
                RetryTask rt = retryTaskMap.get(packet);
                if(!rt.isScheduled()){
                    synchronized (rt){
                        if (!rt.isScheduled()){
                            EventLoop eventLoop = ClientContextHolder.clientContext().channel().eventLoop();
                            rt.setScheduledFuture(eventLoop.scheduleAtFixedRate(() -> {
                                rt.incrementRetryCount();
                                send(packet,this);
                                if (rt.isComplete()){
                                    rt.cancel();
                                    retryTaskMap.remove(packet);
                                }
                            }, 0, 3, TimeUnit.SECONDS));
                            //标记任务已经开始
                            rt.setScheduled(true);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void send(Packet packet, SendMessageListener sendMessageListener) {
        this.messageModel.sendMessage(packet,sendMessageListener);
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    protected void skipScene(String fxmlPath,String title,MessageModel messageModel){
        this.sceneManager.setScene(fxmlPath,title,messageModel,true);
    }

    protected Tuple2<Node, Controller> loadNodeAndController(String fxmlPath){
        Tuple2<Node, Controller> tuple2 = FXMLLoadUtils.loadFxmlAndControl(fxmlPath);
        Controller controller = tuple2.getV2();
        controller.init(messageModel);
        return tuple2;
    }

    protected String loadImageUrl(String originalImageUrl){
        if (imageUrlParse == null){
            try {
                lock.lock();
                if (imageUrlParse == null){
                    imageUrlParse = new ClassPathImageUrlParse();
                }
            }finally {
                lock.unlock();
            }
        }
        return imageUrlParse.loadUrl(originalImageUrl);
    }

}
