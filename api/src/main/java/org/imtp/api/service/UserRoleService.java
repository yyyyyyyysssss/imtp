package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.imtp.api.domain.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {


    List<Long> findRoleIdByUserId(Long userId);

    List<Long> findUserIdByRoleId(Long roleId);

}
