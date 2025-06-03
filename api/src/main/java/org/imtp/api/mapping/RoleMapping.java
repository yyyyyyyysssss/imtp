package org.imtp.api.mapping;

import org.imtp.api.domain.dto.RoleCreateDTO;
import org.imtp.api.domain.dto.RoleUpdateDTO;
import org.imtp.api.domain.entity.Role;
import org.imtp.api.domain.vo.RoleVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @org.mapstruct.Builder(disableBuilder = true))
public interface RoleMapping {

    RoleMapping INSTANCE = Mappers.getMapper(RoleMapping.class);


    Role toRole(RoleCreateDTO roleCreateDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(RoleUpdateDTO roleUpdateDTO, @MappingTarget Role role);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteRole(RoleUpdateDTO roleUpdateDTO, @MappingTarget Role Role);

    @Mapping(source = "createTime",target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updateTime",target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    RoleVO toRoleVO(Role role);

    @IterableMapping(elementTargetType = RoleVO.class)
    List<RoleVO> toRoleVO(List<Role> roles);

}
