package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.mapper.UserRoleMapper;
import org.imtp.api.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public Boolean bindUserRoles(Collection<Long> userIds, Collection<Long> roleIds) {
        if (CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(roleIds)) {
            return true;
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .in(UserRole::getUserId, userIds)
                .in(UserRole::getRoleId,roleIds);
        this.remove(userRoleQueryWrapper);
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

    @Override
    public List<UserRole> findByRoleIds(Collection<Long> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .in(UserRole::getRoleId, roleIds);
        return  this.list(userRoleQueryWrapper);
    }

    @Override
    public List<UserRole> findByUserIds(Collection<Long> userIds) {
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .in(UserRole::getUserId, userIds);
        return this.list(userRoleQueryWrapper);
    }

    @Override
    public Boolean deleteByUserIds(Collection<Long> userIds) {
        QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(UserRole::getUserId, userIds);
        // 删除角色对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }


}
