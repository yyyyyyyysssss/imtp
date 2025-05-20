package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.controller.BaseController;
import org.imtp.api.domain.dto.IdsOnlyDTO;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuQueryDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.vo.MenuVO;
import org.imtp.api.service.MenuService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 9:58
 */
@RequestMapping("/api/system/menu")
@RestController
@Slf4j
public class MenuController extends BaseController {

    @Resource
    private MenuService menuService;

    @PostMapping
    public Result<?> create(@RequestBody @Validated MenuCreateDTO menuCreateDTO) {
        Long id = menuService.create(menuCreateDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> update(@RequestBody @Validated MenuUpdateDTO menuUpdateDTO) {
        Integer affectedRows = menuService.update(menuUpdateDTO);
        return ResultGenerator.ok(affectedRows);
    }

    @GetMapping("/tree")
    public Result<?> tree() {
        List<MenuVO> tree = menuService.tree();
        return ResultGenerator.ok(tree);
    }

    @PostMapping("/query")
    public Result<?> query(@RequestBody MenuQueryDTO menuQueryDTO) {
        PageInfo<MenuVO> menuVOList = menuService.query(menuQueryDTO);
        return ResultGenerator.ok(menuVOList);
    }

    @GetMapping("/{id}")
    public Result<?> details(@PathVariable("id") String id) {
        MenuVO menuVO = menuService.details(id);
        return ResultGenerator.ok(menuVO);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") String id) {
        Integer affectedRows = menuService.delete(id);
        return ResultGenerator.ok(affectedRows);
    }

    @DeleteMapping("/delete")
    public Result<?> batchDelete(@RequestBody @Validated IdsOnlyDTO idsOnlyDTO) {
        Integer affectedRows = menuService.batchDelete(idsOnlyDTO.getId());
        return ResultGenerator.ok(affectedRows);
    }

}
