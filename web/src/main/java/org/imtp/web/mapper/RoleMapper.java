package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.web.domain.entity.Role;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> findRoleByUserIds(@Param("userIds") Collection<Long> userIds);

}
