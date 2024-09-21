package org.imtp.server.feign;


import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "webApi",url = "${im.web.url}",configuration = WebApiRequestInterceptor.class)
public interface WebApi {

    @GetMapping("/api/internal/tokenValid")
    Result<UserInfo> tokenValid(@RequestParam(name = "token") String token);

    @GetMapping("/api/internal/userSession")
    Result<List<UserSessionInfo>> userSession(@RequestParam(name = "userId") String userId);

    @GetMapping("/api/internal/userFriend")
    Result<List<UserFriendInfo>> userFriend(@RequestParam(name = "userId") String userId);

    @GetMapping("/api/internal/userGroup")
    Result<List<UserGroupInfo>> userGroup(@RequestParam(name = "userId") String userId);

    @GetMapping("/api/internal/offlineMessage")
    Result<List<OfflineMessageInfo>> offlineMessage(@RequestParam(name = "userId") String userId);

    @GetMapping("/api/internal/userIds")
    Result<List<String>> userIds(@RequestParam(name = "groupId") String groupId);

    @PostMapping("/api/internal/message")
    Result<Long> message(@RequestBody MessageDTO messageDTO);

    @PostMapping("/api/internal/offlineMessage")
    Result<Boolean> offlineMessage(@RequestBody List<OfflineMessageDTO> offlineMessageList);

}
