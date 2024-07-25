package org.imtp.web.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.imtp.web.enums.LoginType;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/15 9:59
 */
@Getter
@Setter
public class LoginDTO {

    public LoginDTO(){
        this.rememberMe = 0;
    }

    private String username;

    private String password;

    private LoginType loginType = LoginType.NORMAL;

    //是否勾选记住我 1 勾选  0未勾选
    private Integer rememberMe;

    public boolean rememberMe(){
        return rememberMe != null && rememberMe == 1;
    }

}
