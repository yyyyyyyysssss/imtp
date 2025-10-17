package org.imtp.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.imtp.api.domain.dto.TenantCreateDTO;
import org.imtp.api.domain.dto.TenantQueryDTO;
import org.imtp.api.domain.dto.TenantUpdateDTO;
import org.imtp.api.domain.entity.Tenant;
import org.imtp.api.domain.vo.TenantVO;

public interface TenantService extends IService<Tenant> {

    Long create(TenantCreateDTO tenantCreateDTO);

    Boolean update(Long id, TenantUpdateDTO tenantUpdateDTO);

    Boolean updatePatch(Long id, TenantUpdateDTO tenantUpdateDTO);

    PageInfo<TenantVO> queryList(TenantQueryDTO tenantQueryDTO);

    Boolean delete(Long id);

}
