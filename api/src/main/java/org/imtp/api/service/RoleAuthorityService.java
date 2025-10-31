package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.RoleAuthority;

import java.util.List;

public interface RoleAuthorityService extends IService<RoleAuthority> {

    List<RoleAuthority> findByRoleId(Long roleId);

    Boolean bindRoleAuthorities(Long roleId, List<Long> authorityIds);

    Boolean deleteByRoleId(Long roleId);

}
