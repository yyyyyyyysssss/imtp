package org.imtp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Long create(TenantCreateDTO tenantCreateDTO) {
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
    public Boolean update(Long id, TenantUpdateDTO tenantUpdateDTO) {
        Tenant tenant = checkAndResult(id, tenantUpdateDTO);
        TenantMapping.INSTANCE.overwriteTenant(tenantUpdateDTO, tenant);
        int i = tenantMapper.updateById(tenant);
        if (i <= 0) {
            throw new BusinessException("更新租户失败");
        }
        addTenantUser(tenant.getId(), tenantUpdateDTO.getUserIds());
        return true;
    }

    @Override
    @Transactional
    public Boolean updatePatch(Long id, TenantUpdateDTO tenantUpdateDTO) {
        Tenant tenant = checkAndResult(id, tenantUpdateDTO);
        TenantMapping.INSTANCE.updateTenant(tenantUpdateDTO, tenant);
        int i = tenantMapper.updateById(tenant);
        if (i <= 0) {
            throw new BusinessException("更新租户失败");
        }
        if (!CollectionUtils.isEmpty(tenantUpdateDTO.getUserIds())) {
            addTenantUser(tenant.getId(), tenantUpdateDTO.getUserIds());
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

        // 查询租户下的用户
        List<Long> tenantIds = tenants.stream().map(Tenant::getId).toList();
        QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
        tenantUserQueryWrapper
                .lambda()
                .in(TenantUser::getTenantId, tenantIds);
        List<TenantUser> tenantUsers = tenantUserService.list(tenantUserQueryWrapper);
        Map<Long, List<Long>> tenantUserIdMap = tenantUsers.stream().collect(Collectors.groupingBy(
                TenantUser::getTenantId,
                Collectors.mapping(TenantUser::getUserId, Collectors.toList()
                )));
        List<TenantVO> result = new ArrayList<>();
        for (Tenant tenant : tenants) {
            TenantVO tenantVO = TenantMapping.INSTANCE.toTenantVO(tenant);
            List<Long> userIds = tenantUserIdMap.getOrDefault(tenant.getId(), new ArrayList<>());
            tenantVO.setUserIds(userIds);
            result.add(tenantVO);
        }
        PageInfo<TenantVO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        pageInfo.setTotal(tenantPageInfo.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
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

    @Transactional
    public boolean addTenantUser(Long tenantId, Collection<Long> userIds) {
        QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
        tenantUserQueryWrapper
                .lambda()
                .eq(TenantUser::getTenantId, tenantId);
        // 删除原有的租户用户
        tenantUserService.remove(tenantUserQueryWrapper);
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

}
