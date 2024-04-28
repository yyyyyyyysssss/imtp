package org.imtp.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.server.entity.Message;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
