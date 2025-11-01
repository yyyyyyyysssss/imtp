package org.imtp.api.controller.system;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.controller.BaseController;
import org.imtp.api.domain.dto.*;
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

    @PostMapping("/drag")
    public Result<?> drag(@RequestBody @Validated MenuDragDTO menuDragDTO) {
        Boolean b = menuService.menuDrag(menuDragDTO);
        return ResultGenerator.ok(b);
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
    public Result<?> details(@PathVariable("id") Long id) {
        MenuVO menuVO = menuService.details(id);
        return ResultGenerator.ok(menuVO);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        Boolean f = menuService.deleteById(id);
        return ResultGenerator.ok(f);
    }

}
