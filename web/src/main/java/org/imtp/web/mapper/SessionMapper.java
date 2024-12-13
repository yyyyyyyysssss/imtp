package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.web.domain.entity.Session;

import java.util.List;

@Mapper
public interface SessionMapper extends BaseMapper<Session> {

    List<Session> findSessionByUserId(String userId);

}