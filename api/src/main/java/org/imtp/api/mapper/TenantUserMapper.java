package org.imtp.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.imtp.api.domain.entity.TenantUser;

@Mapper
public interface TenantUserMapper extends MPJBaseMapper<TenantUser> {
}
