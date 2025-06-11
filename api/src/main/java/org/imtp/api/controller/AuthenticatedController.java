package org.imtp.api.controller;

import jakarta.annotation.Resource;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.service.AuthenticatedService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 对外提供需要身份认证无需授权的接口
 * @Author ys
 * @Date 2025/6/11 11:41
 */
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedController {

    @Resource
    private AuthenticatedService authenticatedService;

    @GetMapping("/role/options")
    public Result<?> roleOptions() {
        List<RoleVO> roleVOS = authenticatedService.listRoleOptions();
        return ResultGenerator.ok(roleVOS);
    }

    @GetMapping("/authority/options")
    public Result<?> authorityOptions() {
        List<AuthorityVO> authorityVOList = authenticatedService.treeAuthorityOptions();
        return ResultGenerator.ok(authorityVOList);
    }


}
