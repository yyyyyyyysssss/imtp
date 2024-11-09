package org.imtp.web.feign.chatserver;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "chatServerApi",url = "${service.chat:}",fallbackFactory = ChatServerApiFallbackFactory.class)
public interface ChatServerApi {
}
