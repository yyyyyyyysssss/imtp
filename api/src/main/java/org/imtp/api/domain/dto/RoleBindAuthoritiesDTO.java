package org.imtp.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 11:57
 */
@Getter
@Setter
public class RoleBindAuthoritiesDTO {

    private List<Long> authorityIds;

}
