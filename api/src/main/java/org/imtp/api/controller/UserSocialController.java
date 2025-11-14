package org.imtp.api.controller;

import com.github.pagehelper.PageInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.IdOnlyDTO;
import org.imtp.api.domain.dto.UserSessionDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.service.UserSocialService;
import org.imtp.common.packet.body.MessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/api/social")
public class UserSocialController extends BaseController{

    @Resource
    private UserSocialService userSocialService;

    @GetMapping("/userInfo/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<User> userInfo(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        return ResultGenerator.ok(getCurrentUser());
    }

    @GetMapping("/userSession/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserSessionInfo>> userSession(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        List<UserSessionInfo> userSessionInfos = userSocialService.findSessionByUserId(userId);
        return ResultGenerator.ok(userSessionInfos);
    }

    @PostMapping("/userSession/{userId}")
    @CircuitBreaker(name = "slowCallBreaker")
    public Result<Long> userSession(@PathVariable(name = "userId") String userId,@RequestBody @Validated UserSessionDTO userSessionDTO) {
        Long id = userSocialService.createUserSessionByUserId(userId,userSessionDTO);
        return ResultGenerator.ok(id);
    }

    @DeleteMapping("/userSession/{userId}")
    @CircuitBreaker(name = "slowCallBreaker")
    public Result<Boolean> userSession(@PathVariable(name = "userId") String userId, @RequestBody @Validated IdOnlyDTO idOnlyDTO) {
        Boolean deleted = userSocialService.deleteSessionById(idOnlyDTO.getId());
        return deleted ? ResultGenerator.ok() : ResultGenerator.failed();
    }

    @GetMapping("/userFriend/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserFriendInfo>> userFriend(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        List<UserFriendInfo> userFriendInfos = userSocialService.findUserFriendByUserId(userId);
        return ResultGenerator.ok(userFriendInfos);
    }

    @GetMapping("/userGroup/{userId}")
    @CircuitBreaker(name = "commonBreaker", fallbackMethod = "userSocialFallbackMethod")
    public Result<List<UserGroupInfo>> userGroup(@PathVariable(name = "userId") String userId) throws AccessDeniedException {
        List<UserGroupInfo> groupInfos = userSocialService.findUserGroupByUserId(userId);
        return ResultGenerator.ok(groupInfos);
    }

    @GetMapping("/userMessage/{userId}")
    public Result<PageInfo<MessageInfo>> userMessage(@PathVariable(name = "userId") String userId,
                             @RequestParam(name = "sessionId") String sessionId,
                             @RequestParam(name = "prevMsgId",required = false) String prevMsgId,
                             @RequestParam(name = "pageNum", required = false,defaultValue = "1") Integer pageNum,
                             @RequestParam(name = "pageSize", required = false,defaultValue = "20") Integer pageSize) {
        PageInfo<MessageInfo> messageInfoPageInfo = userSocialService.findMessages(userId,sessionId,prevMsgId,pageNum,pageSize);
        return ResultGenerator.ok(messageInfoPageInfo);
    }

    @DeleteMapping("/userMessage/{userId}")
    public Result<Boolean> userMessage(@PathVariable(name = "userId") String userId,@RequestBody @Validated IdOnlyDTO idOnlyDTO){
        Boolean deleted = userSocialService.deleteMessage(idOnlyDTO.getId());
        return ResultGenerator.ok(deleted);
    }

    public Result<?> userSocialFallbackMethod(String userId, Exception exception) {
        log.error("用户:[{}]社交关系查询接口异常: ", userId, exception);
        return ResultGenerator.failed("social service is unavailable");
    }

}
