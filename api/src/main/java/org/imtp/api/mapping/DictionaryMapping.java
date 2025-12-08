package org.imtp.api.mapping;

import org.imtp.api.domain.dto.DictionaryCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemCreateDTO;
import org.imtp.api.domain.dto.DictionaryItemUpdateDTO;
import org.imtp.api.domain.dto.DictionaryUpdateDTO;
import org.imtp.api.domain.entity.Dictionary;
import org.imtp.api.domain.entity.DictionaryItem;
import org.imtp.api.domain.vo.DictionaryItemVO;
import org.imtp.api.domain.vo.DictionaryVO;
import org.imtp.api.domain.vo.RoleVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @org.mapstruct.Builder(disableBuilder = true),uses = {LocalDateTimeMapper.class,LocalDateMapper.class})
public interface DictionaryMapping {

    DictionaryMapping INSTANCE = Mappers.getMapper(DictionaryMapping.class);

    Dictionary toDictionary(DictionaryCreateDTO dictionaryCreateDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDictionary(DictionaryUpdateDTO dictionaryUpdateDTO, @MappingTarget Dictionary dictionary);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteDictionary(DictionaryUpdateDTO dictionaryUpdateDTO, @MappingTarget Dictionary dictionary);

    DictionaryVO toDictionaryVO(Dictionary dictionary);

    @IterableMapping(elementTargetType = DictionaryVO.class)
    List<DictionaryVO> toDictionaryVO(List<Dictionary> dictionaryList);


    DictionaryItem toDictionaryItem(DictionaryItemCreateDTO dictionaryItemCreateDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDictionaryItem(DictionaryItemUpdateDTO dictionaryItemUpdateDTO, @MappingTarget DictionaryItem dictionaryItem);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteDictionaryItem(DictionaryItemUpdateDTO dictionaryItemUpdateDTO, @MappingTarget DictionaryItem dictionaryItem);

    DictionaryItemVO toDictionaryItemVO(DictionaryItem dictionaryItem);

    @IterableMapping(elementTargetType = DictionaryItemVO.class)
    List<DictionaryItemVO> toDictionaryItemVO(List<DictionaryItem> dictionaryItems);

}
