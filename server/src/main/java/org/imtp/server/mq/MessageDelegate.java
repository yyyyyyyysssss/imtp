package org.imtp.server.mq;

public interface MessageDelegate {

    void handleMessage(String message);

}
