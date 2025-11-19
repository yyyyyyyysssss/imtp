package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.config.idwork.IdGen;
import org.imtp.api.domain.dto.TenantCreateDTO;
import org.imtp.api.domain.dto.TenantQueryDTO;
import org.imtp.api.domain.dto.TenantUpdateDTO;
import org.imtp.api.domain.entity.Tenant;
import org.imtp.api.domain.entity.TenantUser;
import org.imtp.api.domain.vo.TenantVO;
import org.imtp.api.enums.TenantStatus;
import org.imtp.api.mapper.TenantMapper;
import org.imtp.api.mapping.TenantMapping;
import org.imtp.api.service.TenantService;
import org.imtp.api.service.TenantUserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
 * @Date 2025/10/16 16:57
 */
@Service
@Slf4j
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TenantUserService tenantUserService;

    @Override
    @Transactional
    public Long createTenant(TenantCreateDTO tenantCreateDTO) {
        Tenant tenant = TenantMapping.INSTANCE.toTenant(tenantCreateDTO);
        tenant.setId(IdGen.genId());
        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setBuiltin(false);
        int row = tenantMapper.insert(tenant);
        if (row <= 0) {
            throw new BusinessException("创建租户失败");
        }
        if (!CollectionUtils.isEmpty(tenantCreateDTO.getUserIds())) {
            addTenantUser(tenant.getId(), tenantCreateDTO.getUserIds());
        }
        return tenant.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "user:tenant", allEntries = true)
    public Boolean updateTenant(Long id, TenantUpdateDTO tenantUpdateDTO, boolean isFullUpdate) {
        Tenant tenant = checkAndResult(id, tenantUpdateDTO);
        if (isFullUpdate) {
            TenantMapping.INSTANCE.overwriteTenant(tenantUpdateDTO, tenant);
        } else {
            TenantMapping.INSTANCE.updateTenant(tenantUpdateDTO, tenant);
        }
        int i = tenantMapper.updateById(tenant);
        if (i <= 0) {
            throw new BusinessException("更新租户失败");
        }
        if(isFullUpdate){
            bindTenantUser(tenant.getId(), tenantUpdateDTO.getUserIds());
        } else {
            if (!CollectionUtils.isEmpty(tenantUpdateDTO.getUserIds())) {
                bindTenantUser(tenant.getId(), tenantUpdateDTO.getUserIds());
            }
        }
        return true;
    }

    private Tenant checkAndResult(Long id, TenantUpdateDTO tenantUpdateDTO) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getBuiltin()) {
            if (tenantUpdateDTO != null && tenantUpdateDTO.getStatus() != null && tenantUpdateDTO.getStatus() != tenant.getStatus()) {
                throw new BusinessException("内置租户不允许修改状态");
            }
        }
        return tenant;
    }

    @Override
    public PageInfo<TenantVO> queryList(TenantQueryDTO tenantQueryDTO) {
        Integer pageNum = tenantQueryDTO.getPageNum();
        Integer pageSize = tenantQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<Tenant> tenantQueryWrapper = new QueryWrapper<>();
        tenantQueryWrapper
                .lambda()
                .eq(Tenant::getIsDeleted, false);
        if (tenantQueryDTO.getKeyword() != null && !tenantQueryDTO.getKeyword().isEmpty()) {
            tenantQueryWrapper
                    .lambda()
                    .like(Tenant::getTenantCode, tenantQueryDTO.getKeyword())
                    .or()
                    .like(Tenant::getTenantName, tenantQueryDTO.getKeyword())
                    .or()
                    .like(Tenant::getContactName, tenantQueryDTO.getKeyword())
                    .or()
                    .like(Tenant::getContactPhone, tenantQueryDTO.getKeyword())
                    .or()
                    .like(Tenant::getContactEmail, tenantQueryDTO.getKeyword());
        }
        if(tenantQueryDTO.getStatus() != null){
            tenantQueryWrapper
                    .lambda()
                    .eq(Tenant::getStatus, tenantQueryDTO.getStatus());
        }
        List<Tenant> tenants = tenantMapper.selectList(tenantQueryWrapper);
        if (tenants == null || tenants.isEmpty()) {
            return new PageInfo<>();
        }
        PageInfo<Tenant> tenantPageInfo = PageInfo.of(tenants);
        List<TenantVO> result = TenantMapping.INSTANCE.toTenantVO(tenants);
        PageInfo<TenantVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(tenantPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public List<Long> findUserIdById(Long tenantId) {

        return tenantUserService.findUserIdByTenantId(tenantId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user:tenant", allEntries = true)
    public Boolean deleteById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getBuiltin()) {
            throw new BusinessException("内置租户不允许删除");
        }
        if(tenant.getStatus().equals(TenantStatus.ACTIVE)){
            throw new BusinessException("使用中的租户不允许删除");
        }
        tenant.setIsDeleted(true);
        return tenantMapper.updateById(tenant) > 0;
    }


    @Override
    public Boolean addTenantUser(Long tenantId,Long userId) {
        TenantUser tenantUser = new TenantUser();
        tenantUser.setId(IdGen.genId());
        tenantUser.setUserId(userId);
        tenantUser.setTenantId(tenantId);
        return tenantUserService.save(tenantUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user:tenant", allEntries = true)
    public Boolean bindTenantUser(Long tenantId,Collection<Long> userIds){
        if (tenantId == null) {
            log.error("bindTenantUser called with empty tenantId");
            return true;
        }
        // 先解绑原有的租户用户
        this.unbindTenantUser(tenantId);
        // 再绑定新的租户用户
        return addTenantUser(tenantId,userIds);
    }

    @CacheEvict(value = "user:tenant", allEntries = true)
    public Boolean unbindTenantUser(Long tenantId){
        if (tenantId == null) {
            log.error("unbindTenantUser called with empty tenantId");
            return true;
        }
        try {
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
            QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
            tenantUserQueryWrapper
                    .lambda()
                    .eq(TenantUser::getTenantId, tenantId);
            // 删除原有的租户用户
            return tenantUserService.remove(tenantUserQueryWrapper);
        }finally {
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }

    }

    @CacheEvict(value = "user:tenant", key = "#userId")
    @Override
    public Boolean unbindUserTenant(Long userId){
        if (userId == null) {
            log.error("unbindUserTenant called with empty userId");
            return true;
        }
        try {
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().tenantLine(true).build());
            QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
            tenantUserQueryWrapper
                    .lambda()
                    .eq(TenantUser::getUserId, userId);
            // 删除原有的租户用户
            return tenantUserService.remove(tenantUserQueryWrapper);
        }finally {
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }

    }

    @Transactional
    public boolean addTenantUser(Long tenantId, Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }
        // 添加新的租户用户
        List<TenantUser> tenantUsers = new ArrayList<>();
        for (Long userId : userIds) {
            TenantUser tenantUser = new TenantUser();
            tenantUser.setId(IdGen.genId());
            tenantUser.setTenantId(tenantId);
            tenantUser.setUserId(userId);
            tenantUsers.add(tenantUser);
        }
        return tenantUserService.saveBatch(tenantUsers);
    }


    @Override
    @Cacheable(value = "user:tenant", key = "#userId")
    public List<TenantVO> findByUserId(Long userId) {
        List<Long> tenantIds = tenantUserService.findTenantIdByUserId(userId);
        if (CollectionUtils.isEmpty(tenantIds)){
            return Collections.emptyList();
        }
        QueryWrapper<Tenant> tenantQueryWrapper = new QueryWrapper<>();
        tenantQueryWrapper
                .lambda()
                .select(Tenant::getId, Tenant::getTenantCode, Tenant::getTenantName, Tenant::getLogo)
                .eq(Tenant::getStatus, TenantStatus.ACTIVE)
                .eq(Tenant::getIsDeleted, false)
                .in(Tenant::getId, tenantIds);
        List<Tenant> tenants = tenantMapper.selectList(tenantQueryWrapper);
        return TenantMapping.INSTANCE.toTenantVO(tenants);
    }
}
