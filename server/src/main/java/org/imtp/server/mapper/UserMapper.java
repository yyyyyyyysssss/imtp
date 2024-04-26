package org.imtp.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.server.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
