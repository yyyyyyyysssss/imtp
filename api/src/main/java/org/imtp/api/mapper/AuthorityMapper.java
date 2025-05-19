package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Mapper
public interface AuthorityMapper extends BaseMapper<Authority>,TreeMapper<Authority> {

    List<Authority> findAuthorityByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    List<Authority> findMenuByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    AuthorityVO findDetailsById(@Param("id") Serializable id);

}
