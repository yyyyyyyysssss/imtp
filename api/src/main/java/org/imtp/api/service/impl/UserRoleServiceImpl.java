package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.domain.vo.RoleVO;
import org.imtp.api.mapper.UserRoleMapper;
import org.imtp.api.service.RoleService;
import org.imtp.api.service.UserRoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
@Service("userRoleService")
@Slf4j
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Resource
    private RoleService roleService;

    @Resource(name = "userRoleService")
    private UserRoleService userRoleServiceProxy;

    @Override
    @Transactional
    @CachePut(value = "user:role",key = "#userId")
    public List<UserRole> bindUserRole(Long userId, Collection<Long> roleIds) {
        if (userId == null) {
            log.warn("bindUserRole userId is null");
            return Collections.emptyList();
        }
        userRoleServiceProxy.deleteByUserId(userId);
        return addUserRole(Collections.singletonList(userId), roleIds);
    }

    @Override
    @Transactional
    @CachePut(value = "role:user",key = "#roleId")
    public List<UserRole> bindRoleUser(Long roleId, Collection<Long> userIds) {
        if (roleId == null) {
            log.warn("bindRoleUser roleId is null");
            return Collections.emptyList();
        }
        userRoleServiceProxy.deleteByRoleId(roleId);
        return addUserRole(userIds, Collections.singletonList(roleId));
    }

    @Transactional
    public List<UserRole> addUserRole(Collection<Long> userIds, Collection<Long> roleIds){
        if(CollectionUtils.isEmpty(userIds) || CollectionUtils.isEmpty(roleIds)){
            return Collections.emptyList();
        }
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
        this.saveBatch(result);
        return result;
    }

    @Override
    @Cacheable(value = "user:role", key = "#userId")
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
    @Cacheable(value = "role:user", key = "#roleId")
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
    @Caching(evict = {
            @CacheEvict(value = "user:role", key = "#userId"),
            @CacheEvict(value = "role:user", allEntries = true)
    })
    public Boolean deleteByUserId(Long userId) {
        List<RoleVO> roles = roleService.findByUserId(userId);
        // 排除超级管理员角色
        List<Long> roleIds = roles.stream().filter(r -> !r.isSuperAdmin()).map(RoleVO::getId).toList();
        if(CollectionUtils.isEmpty(roleIds)){
            return true;
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds);
        // 删除用户对应的角色关联
        return this.remove(userRoleQueryWrapper);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "user:role", allEntries = true),
        @CacheEvict(value = "role:user", key = "#roleId")
    })
    public Boolean deleteByRoleId(Long roleId) {
        RoleVO role = roleService.findById(roleId);
        // 超级管理员角色不删除关联
        if(role.isSuperAdmin()){
            return true;
        }
        QueryWrapper<UserRole> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(UserRole::getRoleId, roleId);
        // 删除角色对应的用户关联
        return this.remove(roleAuthorityQueryWrapper);
    }

}
