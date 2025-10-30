package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserRole;

import java.util.Collection;
import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    Boolean bindUserRoles(Collection<Long> userIds, Collection<Long> roleIds);

    List<UserRole> findByRoleIds(Collection<Long> roleIds);

    List<UserRole> findByUserIds(Collection<Long> userIds);

    Boolean deleteByUserIds(Collection<Long> userIds);

}
