package org.imtp.api.mapping;

import org.imtp.api.domain.dto.AuthorityCreateDTO;
import org.imtp.api.domain.dto.AuthorityUpdateDTO;
import org.imtp.api.domain.dto.MenuCreateDTO;
import org.imtp.api.domain.dto.MenuUpdateDTO;
import org.imtp.api.domain.entity.Authority;
import org.imtp.api.domain.vo.AuthorityUrlVO;
import org.imtp.api.domain.vo.AuthorityVO;
import org.imtp.api.domain.vo.MenuVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAuthority(AuthorityUpdateDTO authorityUpdateDTO,@MappingTarget Authority authority);

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
    @Mapping(target = "authorityUrls", expression = "java(AuthorityMapping.UrlMapperUtil.parse(authority.getUrls()))")
    MenuVO toMenuVo(Authority authority);

    @IterableMapping(elementTargetType = MenuVO.class)
    List<MenuVO> toMenuVo(List<Authority> authorities);


    class UrlMapperUtil {
        public static List<AuthorityUrlVO> parse(String input) {
            if (input == null || input.isEmpty()) {
                return null;
            }
            String[] urls = input.split(",");
            List<AuthorityUrlVO> authorityUrlVOList = new ArrayList<>(urls.length);
            for (String url : urls) {
                AuthorityUrlVO vo = new AuthorityUrlVO();
                String[] parts = url.split(":", 2);
                if (parts.length == 2 && parts[0].toUpperCase().matches("GET|POST|PUT|DELETE|PATCH|OPTIONS|\\*")) {
                    vo.setMethod(parts[0]);
                    vo.setUrl(parts[1]);
                } else {
                    vo.setMethod("*");
                    vo.setUrl(parts[0]);
                }
                authorityUrlVOList.add(vo);
            }
            return authorityUrlVOList;
        }
    }

}
