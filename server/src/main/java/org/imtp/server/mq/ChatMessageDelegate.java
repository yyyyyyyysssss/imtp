package org.imtp.server.mq;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/9 10:43
 */
@Slf4j
public class ChatMessageDelegate implements MessageDelegate{

    @Override
    public void handleMessage(String message) {
        log.info("message:{}",message);
    }

    @Override
    public void handleMessage(byte[] bytes) {
        log.info("bytes:{}",bytes);
    }
}
