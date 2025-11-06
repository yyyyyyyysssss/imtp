package org.imtp.api.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:29
 */
@Getter
@Setter
public class TenantBindUserDTO {

    private List<Long> userIds;

}
