package org.imtp.api.mapping;

import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.MenuVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 11:11
 */
@Mapper(builder = @org.mapstruct.Builder(disableBuilder = true))
public interface AuthorityMapping {

    AuthorityMapping INSTANCE = Mappers.getMapper(AuthorityMapping.class);

    Authority toAuthority(AuthorityCreateDTO authorityAddDTO);

    //部分更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "urls", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    })
    void updateAuthority(AuthorityUpdateDTO authorityUpdateDTO,@MappingTarget Authority authority);

    //全量更新
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void overwriteAuthority(AuthorityUpdateDTO dto, @MappingTarget Authority entity);

    @Mapping(source = "createTime",target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updateTime",target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    AuthorityVO toAuthorityVO(Authority authority);

    @IterableMapping(elementTargetType = AuthorityVO.class)
    List<AuthorityVO> toAuthorityVO(List<Authority> authorities);


    Authority toAuthority(MenuCreateDTO menuCreateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuthority(MenuUpdateDTO menuUpdateDTO,@MappingTarget Authority authority);

    MenuVO toMenuVo(AuthorityVO authorityVO);

    @Mapping(source = "createTime",target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updateTime",target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    MenuVO toMenuVo(Authority authority);

    @IterableMapping(elementTargetType = MenuVO.class)
    List<MenuVO> toMenuVo(List<Authority> authorities);

}
