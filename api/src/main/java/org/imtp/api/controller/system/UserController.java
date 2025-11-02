package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.UserBindRoleDTO;
import org.imtp.api.domain.dto.UserCreateDTO;
import org.imtp.api.domain.dto.UserQueryDTO;
import org.imtp.api.domain.dto.UserUpdateDTO;
import org.imtp.api.domain.vo.UserCreateVO;
import org.imtp.api.domain.vo.UserVO;
import org.imtp.api.service.UserService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 11:32
 */
@RequestMapping("/api/system/user")
@RestController
@Slf4j
public class UserController {


    @Resource
    private UserService userService;

    @PostMapping
    public Result<?> create(@RequestBody @Validated UserCreateDTO userCreateDTO) {
        UserCreateVO userCreateVO = userService.create(userCreateDTO);
        return ResultGenerator.ok(userCreateVO);
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated(value = UserUpdateDTO.UpdateAll.class) UserUpdateDTO userUpdateDTO) {
        Integer affectedRows = userService.update(userUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @PatchMapping
    public Result<?> updatePatch(@RequestBody @Validated UserUpdateDTO userUpdateDTO) {
        Integer affectedRows = userService.updatePartial(userUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @PutMapping("/{id}/password")
    public Result<?> resetPassword(@PathVariable("id") Long id) {
        String newPassword = userService.resetPassword(id);
        return ResultGenerator.ok(newPassword);
    }

    @PostMapping("/{id}/roles")
    public Result<?> bindRoles(@PathVariable Long id, @RequestBody UserBindRoleDTO userBindRoleDTO) {
        Boolean bindRoles = userService.bindRoles(id,userBindRoleDTO.getRoleIds());
        return ResultGenerator.ok(bindRoles);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        Boolean b = userService.deleteById(id);
        return ResultGenerator.ok(b);
    }

    @PostMapping("/query")
    public Result<?> query(@RequestBody UserQueryDTO userQueryDTO) {
        PageInfo<UserVO> pageInfo = userService.queryList(userQueryDTO);
        return ResultGenerator.ok(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<?> details(@PathVariable("id") Long id) {
        UserVO userVO = userService.details(id);
        return ResultGenerator.ok(userVO);
    }

    @PostMapping("/search")
    public Result<?> search(@RequestBody UserQueryDTO userQueryDTO) {
        PageInfo<UserVO> pageInfo = userService.search(userQueryDTO.getPageNum(), userQueryDTO.getPageSize(), userQueryDTO.getKeyword(),userQueryDTO.getIds());
        return ResultGenerator.ok(pageInfo);
    }

}
