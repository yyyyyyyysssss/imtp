package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/7 18:48
 */
@Getter
@Setter
public class UserBindRoleDTO {

    private List<Long> roleIds;

}
