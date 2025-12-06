package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.DictionaryItemCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemQueryDTO;
import org.imtp.api.domain.dto.DictionaryItemUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.entity.DictionaryItem;
import org.imtp.api.domain.vo.DictionaryItemVO;
import org.imtp.api.mapper.DictionaryItemMapper;
import org.imtp.api.mapping.DictionaryMapping;
import org.imtp.api.service.DictionaryItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class DictionaryItemServiceImpl extends ServiceImpl<DictionaryItemMapper, DictionaryItem> implements DictionaryItemService {

    private final DictionaryItemMapper dictionaryItemMapper;

    @Override
    public Long createDictionaryItem(DictionaryItemCreateDTO createDTO) {
        DictionaryItem dictionaryItem = DictionaryMapping.INSTANCE.toDictionaryItem(createDTO);
        dictionaryItem.setId(IdGen.genId());
        if (dictionaryItem.getSort() == null){
            Long parentId = dictionaryItem.getParentId();
            if (parentId == null){
                parentId = 0L;
            }
            int maxSortOfChildren = getMaxSortOfChildren(parentId);
            dictionaryItem.setSort(maxSortOfChildren + 1);
        }
        dictionaryItemMapper.insert(dictionaryItem);
        return dictionaryItem.getId();
    }

    @Override
    public void updateDictionaryItem(DictionaryItemUpdateDTO updateDTO, Boolean isFullUpdate) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(updateDTO.getId());
        if (dictionaryItem == null) {
            throw new BusinessException("该字典项不存在");
        }
        if (isFullUpdate){
            DictionaryMapping.INSTANCE.overwriteDictionaryItem(updateDTO,dictionaryItem);
        } else {
            DictionaryMapping.INSTANCE.updateDictionaryItem(updateDTO,dictionaryItem);
        }
        dictionaryItemMapper.updateById(dictionaryItem);
    }

    @Override
    public void updateStatus(Long id, Boolean enabled) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(id);
        if (dictionaryItem == null) {
            throw new BusinessException("该字典项不存在");
        }
        // 禁用 向下传递
        if(!enabled){
            List<DictionaryItem> childrenDictionaryItems = dictionaryItemMapper.selectChildrenById(id);
            // 子节点都更新
            if (!CollectionUtils.isEmpty(childrenDictionaryItems)){
                LambdaUpdateWrapper<DictionaryItem> updateWrapper = new UpdateWrapper<DictionaryItem>()
                        .lambda()
                        .set(DictionaryItem::getStatus, false)
                        .in(DictionaryItem::getId, childrenDictionaryItems.stream().map(DictionaryItem::getId).collect(Collectors.toSet()));
                dictionaryItemMapper.update(null,updateWrapper);
            }
        } else { // 启用则上下传递
            List<DictionaryItem> dictionaryItems = dictionaryItemMapper.selectLineageById(id);
            if(!CollectionUtils.isEmpty(dictionaryItems)){
                LambdaUpdateWrapper<DictionaryItem> updateWrapper = new UpdateWrapper<DictionaryItem>()
                        .lambda()
                        .set(DictionaryItem::getStatus, true)
                        .in(DictionaryItem::getId, dictionaryItems.stream().map(DictionaryItem::getId).collect(Collectors.toSet()));
                dictionaryItemMapper.update(null,updateWrapper);
            }
        }
    }

    @Override
    public PageInfo<DictionaryItemVO> queryList(DictionaryItemQueryDTO queryDTO) {
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<DictionaryItem> dictionaryItemQueryWrapper = new QueryWrapper<>();
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            dictionaryItemQueryWrapper
                    .lambda()
                    .eq(DictionaryItem::getDictId,queryDTO.getDictId())
                    .like(DictionaryItem::getLabel, queryDTO.getKeyword())
                    .or()
                    .like(DictionaryItem::getValue, queryDTO.getKeyword());
        }
        if (queryDTO.getEnabled() != null) {
            dictionaryItemQueryWrapper.eq("status", queryDTO.getEnabled());
        }
        dictionaryItemQueryWrapper.orderByDesc("sort");
        List<DictionaryItem> dictionaryItems = dictionaryItemMapper.selectList(dictionaryItemQueryWrapper);
        if (dictionaryItems == null || dictionaryItems.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<DictionaryItem> dictionaryItemPageInfo = PageInfo.of(dictionaryItems);
        List<DictionaryItemVO> result = DictionaryMapping.INSTANCE.toDictionaryItemVO(dictionaryItems);
        PageInfo<DictionaryItemVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(dictionaryItemPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public DictionaryItemVO details(Long id) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(id);
        return DictionaryMapping.INSTANCE.toDictionaryItemVO(dictionaryItem);
    }

    @Override
    public void deleteDictionaryItem(Long id) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(id);
        if (dictionaryItem == null) {
            throw new BusinessException("该字典项不存在");
        }
        int i = dictionaryItemMapper.deleteById(id);
        if(i <= 0){
            throw new BusinessException("删除字典项失败，字典项可能不存在");
        }
    }

    public int getMinSortOfChildren(Serializable id, int defaultSort) {
        QueryWrapper<DictionaryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("Min(sort) as sort");
        queryWrapper.eq("parent_id",id);
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectOne(queryWrapper);
        return dictionaryItem != null ? dictionaryItem.getSort() : defaultSort;
    }

    public int getMaxSortOfChildren(Serializable id){
        QueryWrapper<DictionaryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("Max(sort) as sort");
        queryWrapper.eq("parent_id",id);
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectOne(queryWrapper);
        return dictionaryItem != null ? dictionaryItem.getSort() : 0;
    }

}
