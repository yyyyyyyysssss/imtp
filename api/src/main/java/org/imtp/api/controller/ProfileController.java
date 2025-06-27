package org.imtp.api.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.ChangeAvatarDTO;
import org.imtp.api.domain.dto.ChangePasswordDTO;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.UserInfoVO;
import org.imtp.api.service.ProfileService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 当前登录用户的个人信息管理控制器
 * @Author ys
 * @Date 2025/5/19 11:38
 */
@RequestMapping("/api/profile")
@RestController
@Slf4j
public class ProfileController extends BaseController {

    @Resource
    private ProfileService profileService;

    @GetMapping("/user/info")
    public Result<?> userInfo() {
        Long userId = getCurrentUser(User::getId);
        UserInfoVO userInfoVO = profileService.userInfo(userId);
        return ResultGenerator.ok(userInfoVO);
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody @Validated ChangePasswordDTO changePasswordDTO) {
        Long userId = getCurrentUser(User::getId);
        Boolean b = profileService.changePassword(userId, changePasswordDTO);
        return ResultGenerator.ok(b);
    }

    @PutMapping("/avatar")
    public Result<?> changeAvatar(@RequestBody @Validated ChangeAvatarDTO changeAvatarDTO) {
        Long userId = getCurrentUser(User::getId);
        Boolean b = profileService.changeAvatar(userId, changeAvatarDTO.getNewAvatarUrl());
        return ResultGenerator.ok(b);
    }

}
