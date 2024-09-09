package org.imtp.web.controller;

import jakarta.annotation.Resource;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.UserSessionDTO;
import org.imtp.web.domain.entity.User;
import org.imtp.web.service.UserSocialService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/3 17:37
 */
@RestController
@RequestMapping("/social")
public class UserSocialController {

    @Resource
    private UserSocialService userSocialService;


    @GetMapping("/userSession/{userId}")
    public Result<?> userSession(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserSessionInfo> userSessionInfos = userSocialService.userSession(userId);
        return ResultGenerator.ok(userSessionInfos);
    }

    @PostMapping("/userSession")
    public Result<?> userSession(@RequestBody @Validated UserSessionDTO userSessionDTO){
        String id = userSocialService.userSession(userSessionDTO);
        return ResultGenerator.ok(id);
    }

    @GetMapping("/userFriend/{userId}")
    public Result<?> userFriend(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserFriendInfo> userFriendInfos = userSocialService.userFriend(userId);
        return ResultGenerator.ok(userFriendInfos);
    }

    @GetMapping("/userGroup/{userId}")
    public Result<?> userGroup(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserGroupInfo> groupInfos = userSocialService.userGroup(userId);
        return ResultGenerator.ok(groupInfos);
    }

    private void checkUserId(String userId) throws AccessDeniedException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = (User) securityContext.getAuthentication().getPrincipal();
        if(!userId.equals(user.getId().toString())){
            throw new AccessDeniedException("Access Denied");
        }
    }

}
