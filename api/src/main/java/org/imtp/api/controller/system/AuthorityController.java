package org.imtp.api.controller.system;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
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
    public Result<?> createAuthority(@RequestBody @Validated AuthorityCreateDTO authorityCreateDTO) {
        Long id = authorityService.createAuthority(authorityCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> updateAuthority(@RequestBody @Validated AuthorityUpdateDTO authorityUpdateDTO) {
        Boolean f = authorityService.updateAuthority(authorityUpdateDTO,true);
        return ResultGenerator.ok(f);
    }

    @PatchMapping
    public Result<?> modifyAuthority(@RequestBody @Validated AuthorityUpdateDTO authorityUpdateDTO) {
        Boolean f = authorityService.updateAuthority(authorityUpdateDTO,false);
        return ResultGenerator.ok(f);
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
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        Boolean f = authorityService.deleteAuthority(id);
        return ResultGenerator.ok(f);
    }

}
