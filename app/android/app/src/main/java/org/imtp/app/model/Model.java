package org.imtp.app.model;

public interface Model {

    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void removeObserver(Class<? extends Observer> observerClass);

    void notifyObservers(Object object);

}
