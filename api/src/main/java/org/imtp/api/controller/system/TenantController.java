package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.*;
import org.imtp.api.domain.vo.TenantVO;
import org.imtp.api.service.TenantService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:59
 */
@RequestMapping("/api/system/tenant")
@RestController
@Slf4j
public class TenantController {

    @Resource
    private TenantService tenantService;

    @PostMapping
    public Result<Long> create(@RequestBody @Validated TenantCreateDTO tenantCreateDTO) {
        Long id = tenantService.createTenant(tenantCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Validated TenantUpdateDTO tenantUpdateDTO) {
        boolean flag = tenantService.updateTenant(id, tenantUpdateDTO,true);
        return ResultGenerator.ok(flag);
    }

    @PatchMapping("/{id}")
    public Result<Boolean> updatePatch(@PathVariable Long id,@RequestBody TenantUpdateDTO tenantUpdateDTO) {
        boolean flag = tenantService.updateTenant(id, tenantUpdateDTO,false);
        return ResultGenerator.ok(flag);
    }

    @PostMapping("/{id}/users")
    public Result<?> bindUsers(@PathVariable Long id, @RequestBody TenantBindUserDTO tenantBindUserDTO) {
        Boolean flag = tenantService.bindTenantUser(id, tenantBindUserDTO.getUserIds());
        return ResultGenerator.ok(flag);
    }

    @PostMapping("/query")
    public Result<PageInfo<TenantVO>> query(@RequestBody TenantQueryDTO tenantQueryDTO) {
        PageInfo<TenantVO> pageInfo = tenantService.queryList(tenantQueryDTO);
        return ResultGenerator.ok(pageInfo);
    }

    @GetMapping("/{id}/userId")
    public Result<List<Long>> getTenantUserIds(@PathVariable Long id) {
        List<Long> tenantUserIds = tenantService.findUserIdById(id);
        return ResultGenerator.ok(tenantUserIds);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean flag = tenantService.deleteById(id);
        return ResultGenerator.ok(flag);
    }

}
