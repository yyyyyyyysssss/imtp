package org.imtp.web.config.response;

import org.imtp.common.utils.JsonUtil;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/28 10:12
 */
public class Result<T>{

    private Integer code;

    private T data;

    private String message;

    public Result(){}

    public Result(T data){
        this(ResultCode.SUCCEED,data);
    }

    public Result(ResultCode resultCode){
        this(resultCode.getCode(),null,resultCode.getMessage());
    }

    public Result(ResultCode resultCode,T data){
        this(resultCode.getCode(),data,resultCode.getMessage());
    }

    public Result(ResultCode resultCode,String message){
        this(resultCode.getCode(),null,message);
    }

    public Result(ResultCode resultCode,T data,String message){
        this(resultCode.getCode(),data,message);
    }

    public Result(Integer code,T data,String message){
        this.code=code;
        this.data=data;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}