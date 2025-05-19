package org.imtp.api.controller.system;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.dto.IdsOnlyDTO;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.service.AuthorityService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:12
 */
@RequestMapping("/api/system/authority")
@RestController
@Slf4j
public class AuthorityController {

    @Resource
    private AuthorityService authorityService;

    @PostMapping
    public Result<?> create(@RequestBody @Validated AuthorityCreateDTO authorityCreateDTO) {
        Long id = authorityService.create(authorityCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated AuthorityUpdateDTO authorityUpdateDTO) {
        Integer affectedRows = authorityService.update(authorityUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @GetMapping("/{id}")
    public Result<?> details(@PathVariable("id") String id) {
        AuthorityVO details = authorityService.details(id);
        return ResultGenerator.ok(details);
    }

    @GetMapping("/tree")
    public Result<?> tree() {
        List<AuthorityVO> tree = authorityService.tree();
        return ResultGenerator.ok(tree);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") String id) {
        Integer affectedRows = authorityService.delete(id);
        return ResultGenerator.ok(affectedRows);
    }

    @DeleteMapping("/delete")
    public Result<?> batchDelete(@RequestBody @Validated IdsOnlyDTO idsOnlyDTO) {
        Integer affectedRows = authorityService.batchDelete(idsOnlyDTO.getId());
        return ResultGenerator.ok(affectedRows);
    }

}
