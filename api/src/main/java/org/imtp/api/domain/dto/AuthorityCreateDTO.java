package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
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
public class AuthorityCreateDTO {

    @NotBlank(message = "关联菜单id不能为空")
    private String parentId;

    private String rootId;

    @NotBlank(message = "权限编码不能为空")
    private String code;

    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotBlank(message = "资源路径不能为空")
    @ValidApiUrls(message = "资源路径不合法")
    private String urls;

//    public static class AuthorityDTOGroupSequenceProvider implements DefaultGroupSequenceProvider<AuthorityCreateDTO>{
//
//        public AuthorityDTOGroupSequenceProvider(){}
//
//        @Override
//        public List<Class<?>> getValidationGroups(AuthorityCreateDTO authorityDTO) {
//            List<Class<?>> groups = new ArrayList<>();
//            groups.add(AuthorityCreateDTO.class);
//            if(authorityDTO != null){
//                if(AuthorityType.MENU.equals(authorityDTO.getType())){
//                    groups.add(AuthorityCreateDTO.TypeGroup.class);
//                }else if(AuthorityType.BUTTON.equals(authorityDTO.getType())){
//                    groups.add(AuthorityCreateDTO.UrlGroup.class);
//                }
//            }
//            return groups;
//        }
//    }

}
