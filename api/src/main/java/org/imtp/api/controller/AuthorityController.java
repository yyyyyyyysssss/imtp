package org.imtp.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.AuthorityDTO;
import org.imtp.api.domain.dto.AuthorityQueryDTO;
import org.imtp.api.domain.dto.IdsOnlyDTO;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:12
 */
@RequestMapping("/system")
@RestController
@Slf4j
public class AuthorityController {


    @PostMapping("/authority")
    public Result<?> create(@RequestBody @Validated(AuthorityDTO.CreateGroup.class) AuthorityDTO authorityDTO) {

        return ResultGenerator.ok();
    }

    @PutMapping("/authority")
    public Result<?> update(@RequestBody @Validated(AuthorityDTO.UpdateGroup.class) AuthorityDTO authorityDTO) {

        return ResultGenerator.ok();
    }

    @PostMapping("/authority/query")
    public Result<?> query(@RequestBody AuthorityQueryDTO authorityQueryDTO) {

        return ResultGenerator.ok();
    }

    @GetMapping("/authority/{id}")
    public Result<?> details(@PathVariable("id") String id) {

        return ResultGenerator.ok();
    }

    @DeleteMapping("/authority/{id}")
    public Result<?> delete(@PathVariable("id") String id) {

        return ResultGenerator.ok();
    }

    @DeleteMapping("/authority/delete")
    public Result<?> batchDelete(@RequestBody @Validated IdsOnlyDTO idsOnlyDTO) {

        return ResultGenerator.ok();
    }

}
