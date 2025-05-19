package org.imtp.api.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.entity.User;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.service.MenuService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     private MenuService menuService;

     @GetMapping("/menu")
     public Result<?> currentUserMenu() {
         Long userId = getCurrentUser(User::getId);
         List<MenuVO> menuVOList = menuService.getMenuByUserId(userId);
         return ResultGenerator.ok(menuVOList);
     }

}
