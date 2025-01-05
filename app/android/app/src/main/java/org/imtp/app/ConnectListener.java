package org.imtp.app;

public interface ConnectListener {

    void connected();

    void exception(Throwable throwable);

}
