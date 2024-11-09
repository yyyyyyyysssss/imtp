package org.imtp.web.feign.chatserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/9 14:52
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatServerApiFallbackFactory implements FallbackFactory<ChatServerApiFallback> {

    final ChatServerApiFallback chatServerApiFallback;

    @Override
    public ChatServerApiFallback create(Throwable cause) {
        log.error("ChatServerApi Error: ",cause);
        return chatServerApiFallback;
    }
}
