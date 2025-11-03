package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.RoleAuthority;

import java.util.Collection;
import java.util.List;

public interface RoleAuthorityService extends IService<RoleAuthority> {

    List<Long> findRoleIdByAuthorityId(Long authorityId);

    List<Long> findAuthorityIdByRoleId(Long roleId);

    List<Long> findAuthorityIdByRoleId(Collection<Long> roleIds);

}
