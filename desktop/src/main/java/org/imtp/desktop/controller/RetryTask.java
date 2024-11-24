package org.imtp.desktop.controller;

import io.netty.util.concurrent.ScheduledFuture;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/14 12:46
 */
public class RetryTask {

    private ScheduledFuture<?> scheduledFuture;

    private volatile boolean scheduled = false;

    private int retryCount;

    private int initRetry = 0;

    public RetryTask(){
        this.retryCount = 0;
    }

    public RetryTask(int retryCount){
        this.retryCount = retryCount;
    }

    public boolean isScheduled(){
        return this.scheduled;
    }

    public boolean isComplete(){
        return this.retryCount == this.initRetry;
    }

    public void cancel(){
        if (scheduledFuture != null){
            scheduledFuture.cancel(false);
        }
    }

    public void incrementRetryCount(){
        initRetry++;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }
}
