package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
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
@GroupSequenceProvider(AuthorityDTO.AuthorityDTOGroupSequenceProvider.class)
public class AuthorityDTO {

    @NotBlank(message = "权限ID不能为空", groups = {UpdateGroup.class})
    private String id;

    private String parentId;

    private String rootId;

    @NotBlank(message = "权限编码不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private String code;

    @NotBlank(message = "权限名称不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @NotNull(message = "权限类型不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private AuthorityType type;

    @NotBlank(message = "菜单路由不能为空", groups = {TypeGroup.class})
    private String routePath;

    @NotBlank(message = "权限控制的url不能为空", groups = {UrlGroup.class})
    private String urls;

    private String icon;

    public interface CreateGroup {
    }

    public interface UpdateGroup {
    }

    public interface TypeGroup {
    }

    public interface UrlGroup {
    }

    public static class AuthorityDTOGroupSequenceProvider implements DefaultGroupSequenceProvider<AuthorityDTO>{

        @Override
        public List<Class<?>> getValidationGroups(AuthorityDTO authorityDTO) {
            List<Class<?>> groups = new ArrayList<>();
            groups.add(AuthorityDTO.class);
            if(authorityDTO != null && authorityDTO.getType() != null){
                if(authorityDTO.getType().equals(AuthorityType.MENU)){
                    groups.add(TypeGroup.class);
                }else if(authorityDTO.getType().equals(AuthorityType.BUTTON)){
                    groups.add(UrlGroup.class);
                }
            }
            return groups;
        }
    }

}
