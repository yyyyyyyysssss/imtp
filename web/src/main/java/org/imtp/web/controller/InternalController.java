package org.imtp.web.controller;

import jakarta.annotation.Resource;
import org.imtp.common.packet.body.*;
import org.imtp.common.packet.common.MessageDTO;
import org.imtp.common.packet.common.OfflineMessageDTO;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.entity.User;
import org.imtp.web.enums.TokenType;
import org.imtp.web.service.TokenService;
import org.imtp.web.service.UserService;
import org.imtp.web.service.UserSocialService;
import org.imtp.web.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/10 12:48
 */
@RestController
@RequestMapping("/api/internal")
public class InternalController {

    @Resource
    private TokenService tokenService;

    @Resource
    private UserService userService;

    @Resource
    private UserSocialService userSocialService;

    @GetMapping("/tokenValid")
    public Result<UserInfo> tokenValid(@RequestParam("token") String token){
        boolean valid = tokenService.isValid(token, TokenType.ACCESS_TOKEN);
        if (valid){
            String userId = JwtUtil.extractPayloadInfo(token).getSubject();
            User user = userService.findByUserId(userId);
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setGender(user.getGender() != null ? user.getGender().name() : null);
            return ResultGenerator.ok(userInfo);
        }else {
            return ResultGenerator.failed();
        }
    }

    @GetMapping("/userSession")
    public Result<?> userSession(@RequestParam(name = "userId") String userId){
        List<UserSessionInfo> userSessionInfos = userSocialService.findSessionByUserId(userId);
        return ResultGenerator.ok(userSessionInfos);
    }

    @GetMapping("/userFriend")
    public Result<?> userFriend(@RequestParam(name = "userId") String userId){
        List<UserFriendInfo> userFriendInfos = userSocialService.findUserFriendByUserId(userId);
        return ResultGenerator.ok(userFriendInfos);
    }

    @GetMapping("/userGroup")
    public Result<?> userGroup(@RequestParam(name = "userId") String userId){
        List<UserGroupInfo> groupInfos = userSocialService.findUserGroupByUserId(userId);
        return ResultGenerator.ok(groupInfos);
    }

    @GetMapping("/userIds")
    public Result<?> userIds(@RequestParam(name = "groupId") String groupId){
        List<String> userIds = userSocialService.findUserIdByGroupId(groupId);
        return ResultGenerator.ok(userIds);
    }

    @PostMapping("/message")
    public Result<?> message(@RequestBody MessageDTO messageDTO){
        Long messageId = userSocialService.saveMessage(messageDTO);
        return ResultGenerator.ok(messageId);
    }

}
