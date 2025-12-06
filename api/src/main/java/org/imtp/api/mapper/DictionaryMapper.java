package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.api.domain.entity.Dictionary;

@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
}
