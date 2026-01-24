package org.imtp.api.controller.system;


import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.DictionaryItemCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemQueryDTO;
import org.imtp.api.domain.dto.DictionaryItemUpdateDTO;
import org.imtp.api.domain.vo.DictionaryItemVO;
import org.imtp.api.service.DictionaryItemService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/system/dict/item")
@RestController
@Slf4j
@AllArgsConstructor
public class DictionaryItemController {

    private final DictionaryItemService dictionaryItemService;

    @PostMapping
    public Result<Long> createDictionaryItem(@RequestBody @Validated DictionaryItemCreateDTO createDTO) {
        Long id = dictionaryItemService.createDictionaryItem(createDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> updateDictionaryItem(@RequestBody @Validated DictionaryItemUpdateDTO updateDTO) {
        dictionaryItemService.updateDictionaryItem(updateDTO,true);
        return ResultGenerator.ok();
    }

    @PatchMapping
    public Result<?> modifyDictionaryItem(@RequestBody @Validated DictionaryItemUpdateDTO updateDTO) {
        dictionaryItemService.updateDictionaryItem(updateDTO,false);
        return ResultGenerator.ok();
    }

    @PatchMapping("/{id}/disable")
    public Result<?> disable(@PathVariable("id") Long id) {
        dictionaryItemService.updateStatus(id,false);
        return ResultGenerator.ok();
    }

    @PatchMapping("/{id}/enable")
    public Result<?> enable(@PathVariable("id") Long id) {
        dictionaryItemService.updateStatus(id,true);
        return ResultGenerator.ok();
    }

    @PostMapping("/query")
    public Result<PageInfo<DictionaryItemVO>> query(@RequestBody DictionaryItemQueryDTO queryDTO) {
        PageInfo<DictionaryItemVO> pageInfo = dictionaryItemService.queryList(queryDTO);
        return ResultGenerator.ok(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<DictionaryItemVO> details(@PathVariable("id") Long id) {
        DictionaryItemVO dictionaryItemVO = dictionaryItemService.details(id);
        return ResultGenerator.ok(dictionaryItemVO);
    }

    @GetMapping("/{id}/children")
    public Result<List<DictionaryItemVO>> children(@PathVariable("id") Long id) {
        List<DictionaryItemVO> children = dictionaryItemService.findChildrenById(id);
        return ResultGenerator.ok(children);
    }

    @PostMapping("/children/batch")
    public Result<List<DictionaryItemVO>> getBatchChildren(@RequestBody List<Long> ids) {
        List<DictionaryItemVO> children = dictionaryItemService.findChildrenById(ids);
        return ResultGenerator.ok(children);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDictionaryItem(@PathVariable("id") Long id) {
        dictionaryItemService.deleteDictionaryItem(id);
        return ResultGenerator.ok();
    }



}
