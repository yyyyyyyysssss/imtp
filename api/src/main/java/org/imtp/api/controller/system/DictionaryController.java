package org.imtp.api.controller.system;


import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.dto.DictionaryCreateDTO;
import org.imtp.api.domain.dto.DictionaryQueryDTO;
import org.imtp.api.domain.dto.DictionaryUpdateDTO;
import org.imtp.api.domain.vo.DictionaryVO;
import org.imtp.api.service.DictionaryService;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultGenerator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/system/dict")
@RestController
@Slf4j
@AllArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @PostMapping
    public Result<Long> createDictionary(@RequestBody @Validated DictionaryCreateDTO createDTO) {
        Long id = dictionaryService.createDictionary(createDTO);
        return ResultGenerator.ok(id);
    }

    @PutMapping
    public Result<?> updateDictionary(@RequestBody @Validated DictionaryUpdateDTO updateDTO) {
        dictionaryService.updateDictionary(updateDTO,true);
        return ResultGenerator.ok();
    }

    @PatchMapping
    public Result<?> modifyDictionary(@RequestBody @Validated DictionaryUpdateDTO updateDTO) {
        dictionaryService.updateDictionary(updateDTO,false);
        return ResultGenerator.ok();
    }

    @PostMapping("/query")
    public Result<PageInfo<DictionaryVO>> query(@RequestBody DictionaryQueryDTO queryDTO) {
        PageInfo<DictionaryVO> pageInfo = dictionaryService.queryList(queryDTO);
        return ResultGenerator.ok(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<DictionaryVO> details(@PathVariable("id") Long id) {
        DictionaryVO dictionaryVO = dictionaryService.details(id);
        return ResultGenerator.ok(dictionaryVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDictionary(@PathVariable("id") Long id) {
        dictionaryService.deleteDictionary(id);
        return ResultGenerator.ok();
    }


}
