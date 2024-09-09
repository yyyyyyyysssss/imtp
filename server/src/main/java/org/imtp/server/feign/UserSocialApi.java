package org.imtp.server.feign;


import org.imtp.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userSocialApi",url = "${im.web.url}")
public interface UserSocialApi {

    @GetMapping("/userSession/{userId}")
    Result<?> userSession(@PathVariable(name = "userId") String userId);

}
