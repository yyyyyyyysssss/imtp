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
    public Boolean bindUserRole(Long userId, Collection<Long> roleIds) {
        if (userId == null || CollectionUtils.isEmpty(roleIds)) {
            return true;
        }
        deleteByUserId(userId);
        return addUserRole(Collections.singletonList(userId), roleIds);
    }

    @Override
    @Transactional
    public Boolean bindRoleUser(Long roleId, Collection<Long> userIds) {
        if (roleId == null || CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        deleteByRoleId(roleId);
        return addUserRole(userIds, Collections.singletonList(roleId));
    }

    @Transactional
    public Boolean addUserRole(Collection<Long> userIds, Collection<Long> roleIds){
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
    public List<UserRole> findByRoleId(Long roleId) {
        if(roleId == null){
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        return this.list(userRoleQueryWrapper);
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        if(userId == null){
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId);
        return this.list(userRoleQueryWrapper);
    }

    @Override
    public Boolean deleteByUserId(Long userId) {
        QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId);
        // 删除用户对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }

    @Override
    public Boolean deleteByRoleId(Long roleId) {
        QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        // 删除角色对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }
}
