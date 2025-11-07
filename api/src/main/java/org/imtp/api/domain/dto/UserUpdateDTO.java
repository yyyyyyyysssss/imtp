package org.imtp.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/3 15:39
 */
@Getter
@Setter
public class UserUpdateDTO {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "账号不能为空",groups = UserUpdateDTO.UpdateAll.class)
    private String username;

    private String password;

    @NotBlank(message = "用户名不能为空",groups = UserUpdateDTO.UpdateAll.class)
    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private Boolean enabled;

    private List<Long> roleIds;

    public interface UpdateAll{}


}
