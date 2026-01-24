package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.DictionaryCreateDTO;
import org.imtp.api.domain.dto.DictionaryQueryDTO;
import org.imtp.api.domain.dto.DictionaryUpdateDTO;
import org.imtp.api.domain.entity.Dictionary;
import org.imtp.api.domain.vo.DictionaryVO;

public interface DictionaryService extends IService<Dictionary> {

    Long createDictionary(DictionaryCreateDTO createDTO);

    void updateDictionary(DictionaryUpdateDTO updateDTO,Boolean isFullUpdate);

    PageInfo<DictionaryVO> queryList(DictionaryQueryDTO queryDTO);

    DictionaryVO details(Long id);

    DictionaryVO findByCode(String code);

    void deleteDictionary(Long id);

}
