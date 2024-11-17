package org.imtp.web.config.exception;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/16 22:05
 */
public class MinioException extends RuntimeException{

    private String message;

    public MinioException(String message){
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
