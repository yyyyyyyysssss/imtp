package org.imtp.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.server.entity.HistoryMessage;

@Mapper
public interface HistoryMessageMapper extends BaseMapper<HistoryMessage> {
}
