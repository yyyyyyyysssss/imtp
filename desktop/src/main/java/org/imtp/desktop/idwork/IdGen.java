package org.imtp.desktop.idwork;

import org.imtp.common.idwork.RandomWorkIdService;
import org.imtp.common.idwork.SnowflakeIdWorker;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGen {

    private static SnowflakeIdWorker snowflakeIdWorker = null;

    private static final Lock lock = new ReentrantLock();

    public static Long genId() {
        if (snowflakeIdWorker == null) {
            try {
                lock.lock();
                if (snowflakeIdWorker == null) {
                    snowflakeIdWorker = new SnowflakeIdWorker(new RandomWorkIdService());
                }
            } finally {
                lock.unlock();
            }

        }
        return snowflakeIdWorker.nextId();
    }


}
