package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.*;
import org.imtp.api.domain.vo.RoleVO;
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
        Long id = userService.create(userCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated(value = UserUpdateDTO.UpdateAll.class) UserUpdateDTO userUpdateDTO) {
        Integer affectedRows = userService.update(userUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @PatchMapping
    public Result<?> updatePatch(@RequestBody @Validated UserUpdateDTO userUpdateDTO) {
        Integer affectedRows = userService.updatePatch(userUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") String id) {
        Integer affectedRows = userService.delete(id);
        return ResultGenerator.ok(affectedRows);
    }

    @PostMapping("/query")
    public Result<?> query(@RequestBody UserQueryDTO userQueryDTO) {
        PageInfo<UserVO> pageInfo = userService.queryList(userQueryDTO);
        return ResultGenerator.ok(pageInfo);
    }

}
