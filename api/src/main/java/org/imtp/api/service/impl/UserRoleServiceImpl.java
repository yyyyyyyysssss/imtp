package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.entity.UserRole;
import org.imtp.api.mapper.UserRoleMapper;
import org.imtp.api.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 13:34
 */
@Service
@Slf4j
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {


    @Override
    public List<Long> findRoleIdByUserId(Long userId) {
        if (userId == null) {
            log.warn("findRoleIdByUserId called with null userId");
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = this.list(userRoleQueryWrapper);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .distinct()
                .toList();
    }

    @Override
    public List<Long> findUserIdByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("findUserIdByRoleId called with null roleId");
            return Collections.emptyList();
        }
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper
                .lambda()
                .select(UserRole::getUserId)
                .eq(UserRole::getRoleId, roleId);
        List<UserRole> userRoles = this.list(userRoleQueryWrapper);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
        }
        return userRoles.stream()
                .map(UserRole::getUserId)
                .distinct()
                .toList();
    }
}
