package org.imtp.web.config.exception;

/**
 * @Description
 * @Author ys
 * @Date 2024/11/17 10:43
 */
public class DatabaseException extends RuntimeException{

    private String message;

    public DatabaseException(String message){
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
