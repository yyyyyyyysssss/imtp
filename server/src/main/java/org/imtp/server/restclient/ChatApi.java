package org.imtp.server.restclient;


import org.imtp.common.packet.body.UserInfo;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.response.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange(url = "/api/internal")
public interface ChatApi {

    @GetExchange("/tokenValid")
    Result<UserInfo> tokenValid(@RequestParam(name = "token") String token);

    @GetExchange("/userIds")
    Result<List<String>> userIds(@RequestParam(name = "groupId") String groupId);

    @PostExchange("/message")
    Result<Long> message(@RequestBody MessageDTO messageDTO);

}
