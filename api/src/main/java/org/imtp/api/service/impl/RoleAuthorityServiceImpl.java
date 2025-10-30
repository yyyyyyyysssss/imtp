package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.mapper.RoleAuthorityMapper;
import org.imtp.api.service.RoleAuthorityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/5 10:01
 */
@Service
public class RoleAuthorityServiceImpl extends ServiceImpl<RoleAuthorityMapper, RoleAuthority> implements RoleAuthorityService {



    @Override
    public List<RoleAuthority> findByRoleIds(List<Long> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getRoleId, roleIds);
        return this.list(roleAuthorityQueryWrapper);
    }

    @Override
    @Transactional
    public Boolean bindRoleAuthorities(List<Long> roleIds, List<Long> authorityIds) {
        if (CollectionUtils.isEmpty(roleIds) || CollectionUtils.isEmpty(authorityIds)) {
            log.warn("buildRoleAuthorities called with empty roleIds or authorityIds");
            return true;
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getRoleId, roleIds)
                .in(RoleAuthority::getAuthorityId, authorityIds);
        // 删除原有的角色权限
        this.remove(roleAuthorityQueryWrapper);
        // 添加新的角色权限
        List<RoleAuthority> roleAuthorities = new ArrayList<>();
        for (Long roleId : roleIds){
            for (Long authorityId : authorityIds) {
                RoleAuthority roleAuthority = new RoleAuthority();
                roleAuthority.setId(IdGen.genId());
                roleAuthority.setRoleId(roleId);
                roleAuthority.setAuthorityId(authorityId);
                roleAuthorities.add(roleAuthority);
            }
        }
        return this.saveBatch(roleAuthorities);
    }

    @Override
    public Boolean deleteByRoleIds(List<Long> roleIds) {
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .in(RoleAuthority::getRoleId, roleIds);
        // 删除角色对应的权限
        return this.remove(roleAuthorityQueryWrapper);
    }


}
