package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.RoleAuthority;

import java.util.List;

public interface RoleAuthorityService extends IService<RoleAuthority> {

    List<RoleAuthority> findByRoleIds(List<Long> roleIds);

    Boolean bindRoleAuthorities(List<Long> roleIds, List<Long> authorityIds);

    Boolean deleteByRoleIds(List<Long> roleIds);

}
