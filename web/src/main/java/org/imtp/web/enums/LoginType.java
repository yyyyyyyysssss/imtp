package org.imtp.web.enums;

import lombok.Getter;

@Getter
public enum LoginType {
    //普通用户名密码登录
    NORMAL,
    //邮箱验证码登录
    EMAIL
    ;
}
