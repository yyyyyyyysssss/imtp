package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/11 13:53
 */
@Getter
@Setter
public class UserInfoVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private List<String> roleCodes;

    private List<MenuVO> menuTree;

    private List<String> permissionCodes;

}
