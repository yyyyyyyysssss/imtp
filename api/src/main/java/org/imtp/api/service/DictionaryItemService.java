package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.DictionaryItemCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemQueryDTO;
import org.imtp.api.domain.dto.DictionaryItemUpdateDTO;
import org.imtp.api.domain.entity.DictionaryItem;
import org.imtp.api.domain.vo.DictionaryItemVO;

import java.util.List;

public interface DictionaryItemService extends IService<DictionaryItem> {

    Long createDictionaryItem(DictionaryItemCreateDTO createDTO);

    void updateDictionaryItem(DictionaryItemUpdateDTO updateDTO, Boolean isFullUpdate);

    void updateStatus(Long id, Boolean enabled);

    PageInfo<DictionaryItemVO> queryList(DictionaryItemQueryDTO queryDTO);

    DictionaryItemVO details(Long id);

    List<DictionaryItemVO> findChildrenById(Long id);

    List<DictionaryItemVO> findChildrenById(List<Long> ids);

    void deleteDictionaryItem(Long roleId);

    List<DictionaryItemVO> findDictByCode(String code);

}
