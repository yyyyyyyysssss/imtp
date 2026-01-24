package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.DictionaryCreateDTO;
import org.imtp.api.domain.dto.DictionaryQueryDTO;
import org.imtp.api.domain.dto.DictionaryUpdateDTO;
import org.imtp.api.domain.entity.Dictionary;
import org.imtp.api.domain.entity.DictionaryItem;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.DictionaryVO;
import org.imtp.api.mapper.DictionaryMapper;
import org.imtp.api.mapping.DictionaryMapping;
import org.imtp.api.service.DictionaryItemService;
import org.imtp.api.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;


@Service
@Slf4j
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Resource
    private DictionaryMapper dictionaryMapper;

    @Resource
    private DictionaryItemService dictionaryItemService;

    @Override
    public Long createDictionary(DictionaryCreateDTO createDTO) {
        Dictionary dictionary = DictionaryMapping.INSTANCE.toDictionary(createDTO);
        dictionary.setId(IdGen.genId());
        if(dictionary.getEnabled() == null){
            dictionary.setEnabled(true);
        }
        dictionaryMapper.insert(dictionary);
        return dictionary.getId();
    }

    @Override
    public void updateDictionary(DictionaryUpdateDTO updateDTO, Boolean isFullUpdate) {
        Dictionary dictionary = checkAndResult(updateDTO.getId());
        if(isFullUpdate){
            DictionaryMapping.INSTANCE.overwriteDictionary(updateDTO, dictionary);
        } else {
            DictionaryMapping.INSTANCE.updateDictionary(updateDTO, dictionary);
        }
        int i = dictionaryMapper.updateById(dictionary);
        if (i <= 0) {
            throw new BusinessException("更新字典失败");
        }
    }

    @Override
    public PageInfo<DictionaryVO> queryList(DictionaryQueryDTO queryDTO) {
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Dictionary> dictionaryQueryWrapper = new QueryWrapper<>();
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            dictionaryQueryWrapper
                    .lambda()
                    .like(Dictionary::getName, queryDTO.getKeyword())
                    .or()
                    .like(Dictionary::getCode, queryDTO.getKeyword());
        }
        if (queryDTO.getEnabled() != null) {
            dictionaryQueryWrapper.eq("enabled", queryDTO.getEnabled());
        }
        dictionaryQueryWrapper.orderByDesc("sort");
        List<Dictionary> dictionaries = dictionaryMapper.selectList(dictionaryQueryWrapper);
        if (dictionaries == null || dictionaries.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<Dictionary> dictionaryPageInfo = PageInfo.of(dictionaries);
        List<DictionaryVO> result = DictionaryMapping.INSTANCE.toDictionaryVO(dictionaries);
        PageInfo<DictionaryVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(dictionaryPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public DictionaryVO details(Long id) {
        Dictionary dictionary = checkAndResult(id);
        return DictionaryMapping.INSTANCE.toDictionaryVO(dictionary);
    }

    @Override
    public DictionaryVO findByCode(String code) {
        LambdaQueryWrapper<Dictionary> dictionaryLambdaQueryWrapper = new QueryWrapper<Dictionary>()
                .lambda()
                .eq(Dictionary::getCode, code);
        Dictionary dictionary = dictionaryMapper.selectOne(dictionaryLambdaQueryWrapper);
        return DictionaryMapping.INSTANCE.toDictionaryVO(dictionary);
    }

    @Override
    public void deleteDictionary(Long id) {
        checkAndResult(id);
        int i = dictionaryMapper.deleteById(id);
        if(i <= 0){
            throw new BusinessException("删除字典失败，字典可能不存在");
        }
        // 删除字典项
        QueryWrapper<DictionaryItem> dictionaryItemQueryWrapper = new QueryWrapper<>();
        dictionaryItemQueryWrapper.lambda().eq(DictionaryItem::getDictId, id);
        dictionaryItemService.remove(dictionaryItemQueryWrapper);
    }

    private Dictionary checkAndResult(Serializable id){
        Dictionary dictionary = dictionaryMapper.selectById(id);
        if (dictionary == null) {
            throw new BusinessException("字典不存在");
        }
        return dictionary;
    }

}
