package org.imtp.web.config.exception;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/5 13:21
 */
public class OAuth2ClientLoginException extends RuntimeException{

    private String message;

    public OAuth2ClientLoginException(String message){
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }
}
