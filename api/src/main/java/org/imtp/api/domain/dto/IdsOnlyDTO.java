package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/4/8 14:08
 */
@Getter
@Setter
public class IdsOnlyDTO {

    @NotEmpty(message = "ID列表不能为空")
    private List<@NotBlank(message = "ID 不能为空") String> id;

}
