package org.imtp.api.mapping;

import org.imtp.api.domain.dto.TenantCreateDTO;
import org.imtp.api.domain.dto.TenantUpdateDTO;
import org.imtp.api.domain.entity.Tenant;
import org.imtp.api.domain.vo.TenantVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @org.mapstruct.Builder(disableBuilder = true),uses = {DateMapper.class})
public interface TenantMapping {

    TenantMapping INSTANCE = Mappers.getMapper(TenantMapping.class);

    Tenant toTenant(TenantCreateDTO tenantCreateDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTenant(TenantUpdateDTO tenantUpdateDTO, @MappingTarget Tenant tenant);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteTenant(TenantUpdateDTO tenantUpdateDTO, @MappingTarget Tenant tenant);

    TenantVO toTenantVO(Tenant tenant);

    @IterableMapping(elementTargetType = Tenant.class)
    List<TenantVO> toTenantVO(List<Tenant> tenants);

}
