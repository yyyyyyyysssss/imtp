package org.imtp.app.model;

public interface MessageListenerListener {

    void succeed();

    void exception(Throwable throwable);

}
