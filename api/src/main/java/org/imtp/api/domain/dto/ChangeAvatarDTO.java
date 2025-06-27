package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/10 9:15
 */
@Getter
@Setter
public class ChangeAvatarDTO {

    @NotBlank(message = "新头像不能为空")
    private String newAvatarUrl;

}
