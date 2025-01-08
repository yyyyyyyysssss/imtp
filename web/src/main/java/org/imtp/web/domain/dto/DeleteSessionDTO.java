package org.imtp.web.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/1/8 11:06
 */
@Getter
@Setter
public class DeleteSessionDTO {

    @NotNull(message = "id not null")
    private String id;

}
