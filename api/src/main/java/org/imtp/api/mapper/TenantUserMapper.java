package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.api.domain.entity.TenantUser;

@Mapper
public interface TenantUserMapper extends BaseMapper<TenantUser> {
}
