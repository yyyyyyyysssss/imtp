package org.imtp.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.web.domain.entity.Authority;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AuthorityMapper extends BaseMapper<Authority>,TreeMapper<Authority> {

    List<Authority> findAuthorityByRoleIds(@Param("roleIds") Collection<Long> roleIds);

}
