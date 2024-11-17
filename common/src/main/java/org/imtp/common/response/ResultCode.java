package org.imtp.common.response;

public enum ResultCode {

    SUCCEED(0, "请求成功"),
    FAILED(-1, "请求失败，未知错误"),
    USERNAME_OR_PASSWORD_EXCEPTION (4000, "用户名或密码错误"),
    IDENTITY_AUTHENTICATION_EXCEPTION(4001, "身份认证失败"),
    ACCESS_AUTHORIZED_EXCEPTION(4003, "未经授权的访问"),
    PARAM_VALIDATION_EXCEPTION(1003,"参数校验失败"),

    MINIO_EXCEPTION(6012,"Minio操作异常"),

    DATABASE_EXCEPTION(5070,"数据库操作异常"),
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
