package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/10 9:15
 */
@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "原密码不能为空")
    private String originPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
