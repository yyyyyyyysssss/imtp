package org.imtp.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/7 13:30
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateVO {

    private Long id;

    private String initialPassword;

}
