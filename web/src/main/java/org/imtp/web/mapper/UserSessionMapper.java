package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.web.domain.entity.UserSession;

import java.util.List;

@Mapper
public interface UserSessionMapper extends BaseMapper<UserSession> {

    List<UserSessionInfo> findSessionByUserId(@Param("userId") String userId);

}
