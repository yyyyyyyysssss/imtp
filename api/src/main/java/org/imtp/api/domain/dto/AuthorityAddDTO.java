package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.imtp.api.domain.validation.ValidApiUrls;
import org.imtp.api.enums.AuthorityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/16 16:15
 */
@Getter
@Setter
@GroupSequenceProvider(AuthorityAddDTO.AuthorityDTOGroupSequenceProvider.class)
public class AuthorityAddDTO {

    private String parentId;

    private String rootId;

    @NotBlank(message = "权限编码不能为空")
    private String code;

    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotNull(message = "权限类型不能为空")
    private AuthorityType type;

    @NotBlank(message = "菜单路由不能为空", groups = TypeGroup.class)
    private String routePath;

    @NotBlank(message = "资源路径不能为空", groups = UrlGroup.class)
    @ValidApiUrls(groups = UrlGroup.class)
    private String urls;

    private String icon;

    public interface TypeGroup {
    }

    public interface UrlGroup {
    }

    public static class AuthorityDTOGroupSequenceProvider implements DefaultGroupSequenceProvider<AuthorityAddDTO>{

        public AuthorityDTOGroupSequenceProvider(){}

        @Override
        public List<Class<?>> getValidationGroups(AuthorityAddDTO authorityDTO) {
            List<Class<?>> groups = new ArrayList<>();
            groups.add(AuthorityAddDTO.class);
            if(authorityDTO != null){
                if(AuthorityType.MENU.equals(authorityDTO.getType())){
                    groups.add(AuthorityAddDTO.TypeGroup.class);
                }else if(AuthorityType.BUTTON.equals(authorityDTO.getType())){
                    groups.add(AuthorityAddDTO.UrlGroup.class);
                }
            }
            return groups;
        }
    }

}
