package org.imtp.common.response;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/28 10:28
 */
public class ResultGenerator {

    public static <T> Result<T> ok(){

        return ok(null);
    }

    public static <T> Result<T> ok(final T data){

        return new Result<>(data).setSucceed(true);
    }


    public static <T> Result<T> failed(){

        return failed(ResultCode.FAILED);
    }

    public static <T> Result<T> failed(T data){

        return failed(ResultCode.FAILED,data,ResultCode.FAILED.getMessage());
    }

    public static <T> Result<T> failed(String message){

        return failed(ResultCode.FAILED,null,message);
    }

    public static <T> Result<T> failed(Integer code,String message){

        return new Result<>(code,null,message);
    }

    public static <T> Result<T> failed(ResultCode resultCode){

        return failed(resultCode,null,resultCode.getMessage());
    }

    public static <T> Result<T> failed(ResultCode resultCode,String message){

        return failed(resultCode,null,message);
    }

    public static <T> Result<T> failed(ResultCode resultCode,T data){

        return failed(resultCode,data,null);
    }

    public static <T> Result<T> failed(ResultCode resultCode,T data,String message){

        return new Result<>(resultCode,data,message).setSucceed(false);
    }

}
