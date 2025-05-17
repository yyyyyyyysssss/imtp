package org.imtp.api.mapping;

import org.imtp.api.domain.dto.AuthorityAddDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityVO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    Authority toAuthority(AuthorityAddDTO authorityAddDTO);

    Authority toAuthority(AuthorityUpdateDTO authorityUpdateDTO);

    @Mapping(source = "createTime",target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "updateTime",target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    AuthorityVO toAuthorityVO(Authority authority);

    @IterableMapping(elementTargetType = AuthorityVO.class)
    List<AuthorityVO> toAuthorityVO(List<Authority> authorities);

}
