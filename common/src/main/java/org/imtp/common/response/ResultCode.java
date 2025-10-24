package org.imtp.common.response;

public enum ResultCode {

    SUCCEED(0, "请求成功"),
    FAILED(-1, "请求失败，未知错误"),
    IDENTITY_AUTHENTICATION_EXCEPTION(4010, "身份认证失败"),
    USERNAME_OR_PASSWORD_EXCEPTION (4011, "用户名或密码错误"),
    USERNAME_LOCKED_EXCEPTION (4012, "账号已锁定"),
    USERNAME_DISABLED_EXCEPTION (4013, "账号已停用"),
    OAUTH2_CLIENT_LOGIN_EXCEPTION(4019,"oauth2使用三方账号登录失败"),
    ACCESS_AUTHORIZED_EXCEPTION(4030, "未经授权的访问"),
    PARAM_VALIDATION_EXCEPTION(1003,"参数校验失败"),

    MINIO_EXCEPTION(6012,"Minio操作异常"),

    DATABASE_EXCEPTION(5070,"数据库操作异常,请联系管理员"),
    DATABASE_DUPLICATE_KEY_EXCEPTION(5071,"数据存在唯一约束冲突"),
    ;
    private int code;
    private String message;

    ResultCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
