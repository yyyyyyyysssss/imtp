package org.imtp.web.controller;

import jakarta.annotation.Resource;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.UserSessionDTO;
import org.imtp.web.domain.entity.User;
import org.imtp.web.service.UserSocialService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/userInfo")
    public Result<?> userInfo() throws AccessDeniedException {
        User user = currentLoginUser();
        if(user == null){
            throw new AccessDeniedException("Access Denied");
        }
        return ResultGenerator.ok(user);
    }

    @GetMapping("/userInfo/{userId}")
    public Result<?> userInfo(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        User user = currentLoginUser();
        checkUserId(user,userId);
        return ResultGenerator.ok(user);
    }

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

    @GetMapping("/offlineMessage/{userId}")
    public Result<?> offlineMessage(@PathVariable(name = "userId") String userId)  throws AccessDeniedException {
        checkUserId(userId);
        List<OfflineMessageInfo> offlineMessageInfos = userSocialService.offlineMessage(userId);
        return ResultGenerator.ok(offlineMessageInfos);
    }

    private void checkUserId(String userId) throws AccessDeniedException {
        User user = currentLoginUser();
        checkUserId(user,userId);
    }

    private void checkUserId(User user,String userId) throws AccessDeniedException {
        if(!userId.equals(user.getId().toString())){
            throw new AccessDeniedException("Access Denied");
        }
    }

    private User currentLoginUser(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (User) securityContext.getAuthentication().getPrincipal();
    }

}
