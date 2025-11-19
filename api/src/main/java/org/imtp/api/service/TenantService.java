package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.TenantCreateDTO;
import org.imtp.api.domain.dto.TenantQueryDTO;
import org.imtp.api.domain.dto.TenantUpdateDTO;
import org.imtp.api.domain.entity.Tenant;
import org.imtp.api.domain.vo.TenantVO;

import java.util.Collection;
import java.util.List;

public interface TenantService extends IService<Tenant> {

    Long createTenant(TenantCreateDTO tenantCreateDTO);

    Boolean updateTenant(Long id, TenantUpdateDTO tenantUpdateDTO, boolean isFullUpdate);

    PageInfo<TenantVO> queryList(TenantQueryDTO tenantQueryDTO);

    List<Long> findUserIdById(Long tenantId);

    Boolean deleteById(Long id);

    Boolean addTenantUser(Long tenantId,Long userId);

    Boolean bindTenantUser(Long id, Collection<Long> userIds);

    Boolean unbindUserTenant(Long userId);

    List<TenantVO> findByUserId(Long userId);

}
