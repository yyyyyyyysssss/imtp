package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.mapper.UserRoleMapper;
import org.imtp.api.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 13:34
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    @Transactional
    public Boolean buildUserRoles(Collection<Long> userIds, Collection<Long> roleIds) {
        List<UserRole> result = new ArrayList<>();
        for (Long userId : userIds) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setId(IdGen.genId());
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                result.add(userRole);
            }
        }
        return this.saveBatch(result);
    }
}
