package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.imtp.api.domain.entity.RoleAuthority;
import org.imtp.api.mapper.RoleAuthorityMapper;
import org.imtp.api.service.RoleAuthorityService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
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
    public List<Long> findRoleIdByAuthorityId(Long authorityId) {
        if (authorityId == null) {
            log.warn("findRoleIdByAuthorityId called with null authorityId");
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .select(RoleAuthority::getRoleId)
                .eq(RoleAuthority::getAuthorityId, authorityId);
        List<RoleAuthority> roleAuthorities = this.list(roleAuthorityQueryWrapper);
        if (CollectionUtils.isEmpty(roleAuthorities)) {
            return Collections.emptyList();
        }
        return roleAuthorities.stream()
                .map(RoleAuthority::getRoleId)
                .distinct()
                .toList();
    }

    @Override
    public List<Long> findAuthorityIdByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("findAuthorityIdBy called with null roleId");
            return Collections.emptyList();
        }
        return findAuthorityIdByRoleId(Collections.singletonList(roleId));
    }

    @Override
    public List<Long> findAuthorityIdByRoleId(Collection<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            log.warn("findAuthorityIdByRoleId called with empty roleIds");
            return Collections.emptyList();
        }
        QueryWrapper<RoleAuthority> roleAuthorityQueryWrapper = new QueryWrapper<>();
        roleAuthorityQueryWrapper
                .lambda()
                .select(RoleAuthority::getAuthorityId)
                .in(RoleAuthority::getRoleId, roleIds);
        List<RoleAuthority> roleAuthorities = this.list(roleAuthorityQueryWrapper);
        if (CollectionUtils.isEmpty(roleAuthorities)) {
            return Collections.emptyList();
        }
        return roleAuthorities.stream()
                .map(RoleAuthority::getAuthorityId)
                .distinct()
                .toList();
    }
}
