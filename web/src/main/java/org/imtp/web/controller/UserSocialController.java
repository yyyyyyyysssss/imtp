package org.imtp.web.controller;

import com.github.pagehelper.PageInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.body.*;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.imtp.web.domain.dto.DeleteSessionDTO;
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
@Slf4j
@RestController
@RequestMapping("/social")
public class UserSocialController {

    @Resource
    private UserSocialService userSocialService;


    @GetMapping("/userInfo")
    public Result<User> userInfo() throws AccessDeniedException {
        User user = currentLoginUser();
        if (user == null) {
            throw new AccessDeniedException("Access Denied");
        }
        return ResultGenerator.ok(user);
    }

    @GetMapping("/userInfo/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<User> userInfo(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        User user = currentLoginUser();
        checkUserId(user, userId);
        return ResultGenerator.ok(user);
    }

    @GetMapping("/userSession/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserSessionInfo>> userSession(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserSessionInfo> userSessionInfos = userSocialService.findSessionByUserId(userId);
        return ResultGenerator.ok(userSessionInfos);
    }

    @PostMapping("/userSession/{userId}")
    @CircuitBreaker(name = "slowCallBreaker")
    public Result<Long> userSession(@PathVariable(name = "userId") String userId,@RequestBody @Validated UserSessionDTO userSessionDTO) {
        checkUserId(userId);
        Long id = userSocialService.createUserSessionByUserId(userId,userSessionDTO);
        return ResultGenerator.ok(id);
    }

    @DeleteMapping("/userSession/{userId}")
    @CircuitBreaker(name = "slowCallBreaker")
    public Result<Boolean> userSession(@PathVariable(name = "userId") String userId, @RequestBody @Validated DeleteSessionDTO deleteSessionDTO) {
        checkUserId(userId);
        Boolean deleted = userSocialService.deleteSessionById(deleteSessionDTO.getId());
        return deleted ? ResultGenerator.ok() : ResultGenerator.failed();
    }

    @GetMapping("/userFriend/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserFriendInfo>> userFriend(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserFriendInfo> userFriendInfos = userSocialService.findUserFriendByUserId(userId);
        return ResultGenerator.ok(userFriendInfos);
    }

    @GetMapping("/userGroup/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserGroupInfo>> userGroup(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<UserGroupInfo> groupInfos = userSocialService.findUserGroupByUserId(userId);
        return ResultGenerator.ok(groupInfos);
    }

    @GetMapping("/userMessage/{userId}")
    public Result<PageInfo<MessageInfo>> message(@PathVariable(name = "userId") String userId,
                             @RequestParam(name = "sessionId") String sessionId,
                             @RequestParam(name = "pageNum", required = false,defaultValue = "1") Integer pageNum,
                             @RequestParam(name = "pageSize", required = false,defaultValue = "20") Integer pageSize) {
        checkUserId(userId);
        PageInfo<MessageInfo> messageInfoPageInfo = userSocialService.findMessages(userId,sessionId,pageNum,pageSize);
        return ResultGenerator.ok(messageInfoPageInfo);
    }

    @GetMapping("/offlineMessage/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<OfflineMessageInfo>> offlineMessage(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        checkUserId(userId);
        List<OfflineMessageInfo> offlineMessageInfos = userSocialService.findOfflineMessageByUserId(userId);
        return ResultGenerator.ok(offlineMessageInfos);
    }

    private void checkUserId(String userId) throws AccessDeniedException {
        User user = currentLoginUser();
        checkUserId(user, userId);
    }

    private void checkUserId(User user, String userId) throws AccessDeniedException {
        if (!userId.equals(user.getId().toString())) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private User currentLoginUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (User) securityContext.getAuthentication().getPrincipal();
    }

    public Result<?> userSocialFallbackMethod(String userId, Exception exception) {
        log.error("用户:[{}]社交关系查询接口异常: ", userId, exception);
        return ResultGenerator.failed();
    }

}
