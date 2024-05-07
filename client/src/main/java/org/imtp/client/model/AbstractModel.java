package org.imtp.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/7 13:03
 */
public abstract class AbstractModel implements Model{

    private final Lock lock;

    private List<Observer> observers;

    public AbstractModel(){
        observers = new ArrayList<>();
        lock = new ReentrantLock();
    }

    @Override
    public void registerObserver(Observer observer) {
        if(observer == null){
            throw new NullPointerException();
        }
        try {
            lock.lock();
            if(!observers.contains(observer)){
                observers.add(observer);
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        try {
            lock.lock();
            observers.remove(observer);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyObservers(Object object) {
        Object[] arrLocal;
        try {
            lock.lock();
            arrLocal = observers.toArray();
            for (int i = arrLocal.length-1; i >= 0 ; i--) {
                ((Observer)arrLocal[i]).update(object);
            }
        }finally {
            lock.unlock();
        }
    }

}
