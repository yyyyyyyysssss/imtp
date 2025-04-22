package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/8 14:08
 */
@Getter
@Setter
public class IdDTO {

    @NotNull(message = "id not null")
    private String id;

}
