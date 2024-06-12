package org.imtp.client.constant;

public interface SendMessageListener {

    boolean isSuccess();


    boolean isCancelled();

    void exception(Throwable throwable);

}
