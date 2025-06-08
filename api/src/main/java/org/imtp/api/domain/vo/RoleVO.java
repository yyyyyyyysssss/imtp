package org.imtp.api.domain.vo;

import lombok.Getter;
import lombok.Setter;
import org.imtp.api.enums.RoleType;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:41
 */
@Getter
@Setter
public class RoleVO {

    private Long id;

    private String code;

    private String name;

    private Boolean enabled;

    private RoleType type;

    private String createTime;

    private String updateTime;

    private List<Long> authorityIds;

}
