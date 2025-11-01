package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.*;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.service.RoleService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:35
 */
@RequestMapping("/api/system/role")
@RestController
@Slf4j
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping
    public Result<?> create(@RequestBody @Validated RoleCreateDTO roleCreateDTO) {
        Long id = roleService.create(roleCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated RoleUpdateDTO roleUpdateDTO) {
        Integer affectedRows = roleService.update(roleUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @PostMapping("/{id}/authorities")
    public Result<?> bindAuthorities(@PathVariable Long id, @RequestBody RoleBindAuthoritiesDTO roleBindAuthoritiesDTO) {
        Boolean bindAuthority = roleService.bindAuthorities(id,roleBindAuthoritiesDTO.getAuthorityIds());
        return ResultGenerator.ok(bindAuthority);
    }

    @PostMapping("/{id}/users")
    public Result<?> bindUsers(@PathVariable Long id, @RequestBody RoleBindUserDTO roleBindUserDTO) {
        Boolean bindAuthority = roleService.bindUsers(id,roleBindUserDTO.getUserIds());
        return ResultGenerator.ok(bindAuthority);
    }

    @PatchMapping
    public Result<?> updatePatch(@RequestBody @Validated RoleUpdateDTO roleUpdateDTO) {
        Integer affectedRows = roleService.updatePartial(roleUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        Boolean f = roleService.deleteById(id);
        return ResultGenerator.ok(f);
    }

    @GetMapping("/{id}")
    public Result<RoleVO> details(@PathVariable("id") Long id) {
        RoleVO roleVO = roleService.details(id);
        return ResultGenerator.ok(roleVO);
    }

    @PostMapping("/query")
    public Result<?> query(@RequestBody RoleQueryDTO roleQueryDTO) {
        PageInfo<RoleVO> pageInfo = roleService.queryList(roleQueryDTO);
        return ResultGenerator.ok(pageInfo);
    }


}
