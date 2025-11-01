package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.mapper.RoleAuthorityMapper;
import org.imtp.api.service.RoleAuthorityService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/5 10:01
 */
@Service("roleAuthorityService")
public class RoleAuthorityServiceImpl extends ServiceImpl<RoleAuthorityMapper, RoleAuthority> implements RoleAuthorityService {


    @Resource(name = "roleAuthorityService")
    private RoleAuthorityService roleAuthorityServiceProxy;

    @Override
    @Cacheable(value = "role:authority", key = "#roleId")
    public List<RoleAuthority> findByRoleId(Long roleId) {
        if(roleId == null){
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        return this.list(roleAuthorityQueryWrapper);
    }

    @Override
    public List<RoleAuthority> findByRoleId(Collection<Long> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            log.warn("findByRoleId called with empty roleIds");
            return Collections.emptyList();
        }
        return roleIds.stream()
                .map(roleAuthorityServiceProxy::findByRoleId)
                .flatMap(Collection::stream)
                .toList();
    }


    @Override
    @Transactional
    @CachePut(value = "role:authority",key = "#roleId")
    public List<RoleAuthority> bindRoleAuthorities(Long roleId, List<Long> authorityIds) {
        if (roleId == null) {
            log.warn("buildRoleAuthorities called with empty roleId");
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        // 删除原有的角色权限
        this.remove(roleAuthorityQueryWrapper);
        if(CollectionUtils.isEmpty(authorityIds)){
            return Collections.emptyList();
        }
        // 添加新的角色权限
        List<RoleAuthority> roleAuthorities = new ArrayList<>();
        for (Long authorityId : authorityIds) {
            RoleAuthority roleAuthority = new RoleAuthority();
            roleAuthority.setId(IdGen.genId());
            roleAuthority.setRoleId(roleId);
            roleAuthority.setAuthorityId(authorityId);
            roleAuthorities.add(roleAuthority);
        }
        this.saveBatch(roleAuthorities);
        return roleAuthorities;
    }

    @Override
    @CacheEvict(value = "role:authority", key = "#roleId")
    public Boolean deleteByRoleId(Long roleId) {
        if(roleId == null){
            log.warn("deleteByRoleId called with null roleId");
            return true;
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .eq(RoleAuthority::getRoleId, roleId);
        // 删除角色对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }


    @Override
    @CacheEvict(value = "role:authority", allEntries = true)
    public Boolean deleteByAuthorityId(Long authorityId) {
        if(authorityId == null){
            log.warn("deleteByAuthorityId called with null authorityId");
            return true;
        }
        // 删除角色对应的权限
        return deleteByAuthorityId(Collections.singletonList(authorityId));
    }

    @Override
    @CacheEvict(value = "role:authority", allEntries = true)
    public Boolean deleteByAuthorityId(Collection<Long> authorityIds) {
        if(CollectionUtils.isEmpty(authorityIds)){
            log.warn("deleteByAuthorityId called with empty authorityIds");
            return true;
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getAuthorityId, authorityIds);
        // 删除角色对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }


}
