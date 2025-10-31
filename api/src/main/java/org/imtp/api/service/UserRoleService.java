package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserRole;

import java.util.Collection;
import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    Boolean bindUserRole(Long userId, Collection<Long> roleIds);

    Boolean bindRoleUser(Long roleId, Collection<Long> userIds);

    List<UserRole> findByRoleId(Long roleId);

    List<UserRole> findByUserId(Long userId);

    Boolean deleteByUserId(Long userId);

    Boolean deleteByRoleId(Long roleId);

}
