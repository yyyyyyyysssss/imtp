package org.imtp.desktop.model;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/7 12:16
 */
public interface Model {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void removeObserver(Class<? extends Observer> observerClass);

    void notifyObservers(Object object);



}
