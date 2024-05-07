package org.imtp.client.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import org.imtp.client.model.Model;
import org.imtp.client.model.Observer;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/7 12:27
 */
public abstract class ModelHandlerAdapter<T> extends SimpleChannelInboundHandler<T> implements Model {

    private Model messageModel;

    public ModelHandlerAdapter(Model messageModel){
        this.messageModel = messageModel;
    }

    @Override
    public void registerObserver(Observer observer) {
        messageModel.registerObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        messageModel.removeObserver(observer);
    }

    @Override
    public void notifyObservers(Object object) {
        messageModel.notifyObservers(object);
    }
}
