package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.api.domain.entity.UserMfa;

@Mapper
public interface UserMfaMapper extends BaseMapper<UserMfa> {
}
