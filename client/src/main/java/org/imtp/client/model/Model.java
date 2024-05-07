package org.imtp.client.model;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/7 12:16
 */
public interface Model {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Object object);

}
