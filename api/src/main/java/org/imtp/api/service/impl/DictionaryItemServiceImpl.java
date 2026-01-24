package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.config.redis.RedisHelper;
import org.imtp.api.domain.dto.DictionaryItemCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemQueryDTO;
import org.imtp.api.domain.dto.DictionaryItemUpdateDTO;
import org.imtp.api.domain.entity.Dictionary;
import org.imtp.api.domain.entity.DictionaryItem;
import org.imtp.api.domain.vo.DictionaryItemVO;
import org.imtp.api.domain.vo.DictionaryVO;
import org.imtp.api.mapper.DictionaryItemMapper;
import org.imtp.api.mapping.DictionaryMapping;
import org.imtp.api.service.DictionaryItemService;
import org.imtp.api.service.DictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DictionaryItemServiceImpl extends ServiceImpl<DictionaryItemMapper, DictionaryItem> implements DictionaryItemService {

    @Resource
    private DictionaryItemMapper dictionaryItemMapper;

    @Resource
    private DictionaryService dictionaryService;

    @Resource
    private RedisHelper redisHelper;

    private final String cachePrefix = "dict:";

    @Override
    public Long createDictionaryItem(DictionaryItemCreateDTO createDTO) {
        DictionaryItem dictionaryItem = DictionaryMapping.INSTANCE.toDictionaryItem(createDTO);
        dictionaryItem.setId(IdGen.genId());
        Long parentId = dictionaryItem.getParentId();
        int level;
        if (parentId == null) {
            parentId = 0L;
            level = 1;
        } else {
            DictionaryItem parentDictionaryItem = dictionaryItemMapper.selectById(parentId);
            level = parentDictionaryItem.getLevel() + 1;
        }
        if (dictionaryItem.getSort() == null) {
            int maxSortOfChildren = getMaxSortOfChildren(dictionaryItem.getDictId(), parentId);
            dictionaryItem.setSort(maxSortOfChildren + 1);
        }
        dictionaryItem.setParentId(parentId);
        dictionaryItem.setLevel(level);
        dictionaryItemMapper.insert(dictionaryItem);
        // 删除缓存
        Dictionary dictionary = dictionaryService.getById(dictionaryItem.getDictId());
        String cacheKey = cachePrefix + dictionary.getCode();
        redisHelper.delete(cacheKey);
        return dictionaryItem.getId();
    }

    @Override
    public void updateDictionaryItem(DictionaryItemUpdateDTO updateDTO, Boolean isFullUpdate) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(updateDTO.getId());
        if (dictionaryItem == null) {
            throw new BusinessException("该字典项不存在");
        }
        Boolean originEnabled = dictionaryItem.getEnabled();
        if (isFullUpdate) {
            DictionaryMapping.INSTANCE.overwriteDictionaryItem(updateDTO, dictionaryItem);
        } else {
            DictionaryMapping.INSTANCE.updateDictionaryItem(updateDTO, dictionaryItem);
        }
        dictionaryItemMapper.updateById(dictionaryItem);
        if (updateDTO.getEnabled() != null && !updateDTO.getEnabled().equals(originEnabled)) {
            this.updateStatus(dictionaryItem.getId(), updateDTO.getEnabled());
        }
        // 删除缓存
        Dictionary dictionary = dictionaryService.getById(dictionaryItem.getDictId());
        String cacheKey = cachePrefix + dictionary.getCode();
        redisHelper.delete(cacheKey);
    }

    @Override
    public void updateStatus(Long id, Boolean enabled) {
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectById(id);
        if (dictionaryItem == null) {
            throw new BusinessException("该字典项不存在");
        }
        // 子节点都更新
        List<DictionaryItem> childrenDictionaryItems = dictionaryItemMapper.selectChildrenById(id);
        // 子节点都更新
        if (!CollectionUtils.isEmpty(childrenDictionaryItems)) {
            LambdaUpdateWrapper<DictionaryItem> updateWrapper = new UpdateWrapper<DictionaryItem>()
                    .lambda()
                    .set(DictionaryItem::getEnabled, enabled)
                    .in(DictionaryItem::getId, childrenDictionaryItems.stream().map(DictionaryItem::getId).collect(Collectors.toSet()));
            dictionaryItemMapper.update(null, updateWrapper);
        }
        // 启用则再向上传递
        if (enabled) {
            List<DictionaryItem> dictionaryItems = dictionaryItemMapper.selectParentById(id);
            if (!CollectionUtils.isEmpty(dictionaryItems)) {
                LambdaUpdateWrapper<DictionaryItem> updateWrapper = new UpdateWrapper<DictionaryItem>()
                        .lambda()
                        .set(DictionaryItem::getEnabled, true)
                        .in(DictionaryItem::getId, dictionaryItems.stream().map(DictionaryItem::getId).collect(Collectors.toSet()));
                dictionaryItemMapper.update(null, updateWrapper);
            }
        }
        // 删除缓存
        Dictionary dictionary = dictionaryService.getById(dictionaryItem.getDictId());
        String cacheKey = cachePrefix + dictionary.getCode();
        redisHelper.delete(cacheKey);
    }

    @Override
    public PageInfo<DictionaryItemVO> queryList(DictionaryItemQueryDTO queryDTO) {
        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<DictionaryItem> dictionaryItemQueryWrapper = new QueryWrapper<>();
        dictionaryItemQueryWrapper
                .lambda()
                .eq(DictionaryItem::getDictId, queryDTO.getDictId())
                .eq(DictionaryItem::getLevel, 1);
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            dictionaryItemQueryWrapper
                    .lambda()
                    .like(DictionaryItem::getLabel, queryDTO.getKeyword())
                    .or()
                    .like(DictionaryItem::getValue, queryDTO.getKeyword());
        }
        if (queryDTO.getEnabled() != null) {
            dictionaryItemQueryWrapper.eq("enabled", queryDTO.getEnabled());
        }
        dictionaryItemQueryWrapper.orderByAsc("sort is null,sort");
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
    public List<DictionaryItemVO> findChildrenById(Long id) {
        List<DictionaryItemVO> result = findChildrenById(Collections.singletonList(id));
        if(CollectionUtils.isEmpty(result)){
            return Collections.emptyList();
        }
        return result.getFirst().getChildren();
    }

    @Override
    public List<DictionaryItemVO> findChildrenById(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<DictionaryItem> dictionaryItemsList = dictionaryItemMapper.selectChildrenByIds(ids);
        if (CollectionUtils.isEmpty(dictionaryItemsList)) {
            return Collections.emptyList();
        }
        // 将 DictionaryItem 转换为 DictionaryItemVO，并进行排序（null排后）
        List<DictionaryItemVO> dictionaryItemVOList = dictionaryItemsList.stream()
                .map(DictionaryMapping.INSTANCE::toDictionaryItemVO)
                .sorted(Comparator.comparing(
                                DictionaryItemVO::getSort,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(
                                DictionaryItemVO::getCreateTime,
                                Comparator.reverseOrder()))
                .toList();

        // 使用 LinkedHashMap 保证按照 ids 顺序分组
        Map<Long, List<DictionaryItemVO>> parentMap = dictionaryItemVOList.stream()
                .collect(Collectors.groupingBy(
                        DictionaryItemVO::getParentId,
                        LinkedHashMap::new, // 使用 LinkedHashMap 保证顺序
                        Collectors.toList()
                ));

        List<DictionaryItemVO> result = new ArrayList<>();
        for (Long id : ids){
            DictionaryItemVO dictionaryItemVO = dictionaryItemsList.stream()
                    .filter(f -> f.getId().equals(id))
                    .findFirst()
                    .map(DictionaryMapping.INSTANCE::toDictionaryItemVO)
                    .orElseThrow();
            List<DictionaryItemVO> children = parentMap.get(id);
            if (children != null) {
                dictionaryItemVO.setChildren(children);
            } else {
                dictionaryItemVO.setChildren(Collections.emptyList());
            }
            result.add(dictionaryItemVO);
        }
        return result;
    }

    @Override
    public void deleteDictionaryItem(Long id) {
        // 删除自身及其子项
        List<DictionaryItem> dictionaryItems = dictionaryItemMapper.selectChildrenById(id);
        if (CollectionUtils.isEmpty(dictionaryItems)) {
            throw new BusinessException("该字典项不存在");
        }
        dictionaryItemMapper.deleteByIds(dictionaryItems.stream().map(DictionaryItem::getId).collect(Collectors.toList()));
        // 删除缓存
        Dictionary dictionary = dictionaryService.getById(dictionaryItems.getFirst().getDictId());
        String cacheKey = cachePrefix + dictionary.getCode();
        redisHelper.delete(cacheKey);
    }

    @Override
    public List<DictionaryItemVO> findDictByCode(String code) {
        String cacheKey = cachePrefix + code;
        List<DictionaryItemVO> dictionaryItemVOS = redisHelper.getValue(cacheKey, new TypeReference<List<DictionaryItemVO>>() {
        });
        if (!CollectionUtils.isEmpty(dictionaryItemVOS)) {
            return dictionaryItemVOS;
        }
        DictionaryVO dictionaryVO = dictionaryService.findByCode(code);
        if (dictionaryVO == null) {
            throw new BusinessException("字典不存在");
        }
        if (!dictionaryVO.getEnabled()) {
            throw new BusinessException("字典已停用");
        }
        Long dictId = dictionaryVO.getId();
        LambdaQueryWrapper<DictionaryItem> dictionaryItemLambdaQueryWrapper = new QueryWrapper<DictionaryItem>()
                .lambda()
                .select(
                        DictionaryItem::getId,
                        DictionaryItem::getParentId,
                        DictionaryItem::getLabel,
                        DictionaryItem::getValue,
                        DictionaryItem::getAlias,
                        DictionaryItem::getCategory,
                        DictionaryItem::getImgUrl
                )
                .eq(DictionaryItem::getDictId, dictId)
                .eq(DictionaryItem::getEnabled, true)
                .orderByAsc(DictionaryItem::getSort);
        List<DictionaryItem> dictionaryItems = dictionaryItemMapper.selectList(dictionaryItemLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(dictionaryItems)) {
            return Collections.emptyList();
        }
        List<DictionaryItemVO> dictionaryItemVO = DictionaryMapping.INSTANCE.toDictionaryItemVO(dictionaryItems);
        redisHelper.setValue(cacheKey, dictionaryItemVO, Duration.ofHours(2));
        return dictionaryItemVO;
    }

    public int getMinSortOfChildren(Serializable dictId, Serializable id, int defaultSort) {
        QueryWrapper<DictionaryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("Min(sort) as sort");
        queryWrapper.eq("dict_id", dictId);
        queryWrapper.eq("parent_id", id);
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectOne(queryWrapper);
        return dictionaryItem != null ? dictionaryItem.getSort() : defaultSort;
    }

    public int getMaxSortOfChildren(Serializable dictId, Serializable id) {
        QueryWrapper<DictionaryItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("Max(sort) as sort");
        queryWrapper.eq("dict_id", dictId);
        queryWrapper.eq("parent_id", id);
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectOne(queryWrapper);
        return dictionaryItem != null ? dictionaryItem.getSort() : 0;
    }

}
