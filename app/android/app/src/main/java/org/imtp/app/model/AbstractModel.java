package org.imtp.app.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractModel implements Model {

    private final List<Observer> observers;

    public AbstractModel(){
        observers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer == null){
            throw new NullPointerException("observer not null");
        }
        if(!observers.contains(observer)){
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if (observers.isEmpty()){
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void removeObserver(Class<? extends Observer> observerClass) {
        if (observers.isEmpty()){
            return;
        }
        observers.removeIf(observerClass::isInstance);
    }

    @Override
    public void notifyObservers(Object object) {
        Object[] arrLocal = observers.toArray();
        for (int i = arrLocal.length-1; i >= 0 ; i--) {
            ((Observer)arrLocal[i]).update(object);
        }
    }

}
