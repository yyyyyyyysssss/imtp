package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.domain.entity.TenantUser;
import org.imtp.api.mapper.TenantUserMapper;
import org.imtp.api.service.TenantUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 17:30
 */
@Service
@Slf4j
public class TenantUserServiceImpl extends ServiceImpl<TenantUserMapper, TenantUser> implements TenantUserService {

    @Override
    public List<Long> findUserIdByTenantId(Long tenantId) {
        QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
        tenantUserQueryWrapper
                .lambda()
                .select(TenantUser::getUserId)
                .eq(TenantUser::getTenantId, tenantId);
        return this.list(tenantUserQueryWrapper)
                .stream()
                .map(TenantUser::getUserId)
                .distinct()
                .toList();
    }

}
