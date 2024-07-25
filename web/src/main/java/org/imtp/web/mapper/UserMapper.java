package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.web.domain.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
