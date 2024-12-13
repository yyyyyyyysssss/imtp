package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.common.packet.body.MessageInfo;
import org.imtp.web.domain.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    List<MessageInfo> findMessageByIds(@Param("msgIds") List<Long> msgIds);

}
