package org.imtp.web.config;

import org.imtp.common.response.ResultCode;

/**
 * @Description
 * @Author ys
 * @Date 2023/5/11 10:32
 */
public class BusinessException extends RuntimeException{

    public int code;

    public String reason;

    public BusinessException(ResultCode resultCode){
        this(resultCode.getCode(),resultCode.getMessage());
    }

    public BusinessException(String reason){
        this(ResultCode.FAILED.getCode(),reason);
    }

    public BusinessException(Throwable throwable){
        this(ResultCode.FAILED.getCode(),throwable.getMessage());
    }

    public BusinessException(int code,String reason){
        this.code=code;
        this.reason=reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
