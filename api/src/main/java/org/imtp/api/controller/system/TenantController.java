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
        Long id = tenantService.create(tenantCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody @Validated TenantUpdateDTO tenantUpdateDTO) {
        boolean flag = tenantService.update(tenantUpdateDTO);
        return ResultGenerator.ok(flag);
    }

    @PostMapping("/query")
    public Result<PageInfo<TenantVO>> query(@RequestBody TenantQueryDTO tenantQueryDTO) {
        PageInfo<TenantVO> pageInfo = tenantService.queryList(tenantQueryDTO);
        return ResultGenerator.ok(pageInfo);
    }

}
