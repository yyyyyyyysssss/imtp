package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.web.domain.entity.OfflineMessage;

@Mapper
public interface OfflineMessageMapper extends BaseMapper<OfflineMessage> {
}
