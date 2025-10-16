package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserRole;

import java.util.Collection;

public interface UserRoleService extends IService<UserRole> {

    Boolean buildUserRoles(Collection<Long> userIds, Collection<Long> roleIds);

}
