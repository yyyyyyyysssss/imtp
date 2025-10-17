package org.imtp.api.config.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.imtp.common.response.Result;
import org.imtp.common.response.ResultCode;
import org.imtp.common.response.ResultGenerator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description 全局异常处理器
 * @Author ys
 * @Date 2023/4/28 13:42
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AuthenticationException.class})
    public Result<?> handlerAuthenticationException(AuthenticationException authenticationException){
        if(authenticationException instanceof  BadCredentialsException
                || authenticationException instanceof UsernameNotFoundException
        ){
            log.error("用户名或密码错误: ",authenticationException);
            return ResultGenerator.failed(ResultCode.USERNAME_OR_PASSWORD_EXCEPTION);
        }
        log.error("认证异常: ",authenticationException);
        return ResultGenerator.failed(ResultCode.IDENTITY_AUTHENTICATION_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({AccountStatusException.class})
    public Result<?> handlerAccountStatusException(AccountStatusException accountStatusException){
        if(accountStatusException instanceof LockedException){
            log.error("账号已锁定: ",accountStatusException);
            return ResultGenerator.failed(ResultCode.USERNAME_LOCKED_EXCEPTION);
        }
        if(accountStatusException instanceof DisabledException){
            log.error("账号已停用: ",accountStatusException);
            return ResultGenerator.failed(ResultCode.USERNAME_DISABLED_EXCEPTION);
        }
        log.error("账号状态异常: ",accountStatusException);
        return ResultGenerator.failed(ResultCode.USERNAME_LOCKED_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public Result<?> handlerAccessDeniedException(AccessDeniedException accessDeniedException){
        log.error("Access Denied: ",accessDeniedException);
        return ResultGenerator.failed(ResultCode.ACCESS_AUTHORIZED_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler({RequestNotPermitted.class})
    public Result<?> handlerRequestNotPermitted(RequestNotPermitted requestNotPermitted){
        log.error("限流: ",requestNotPermitted);
        return ResultGenerator.failed("请求过于频繁，请稍后再试");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessException(BusinessException e){
        log.error("业务异常: ",e);
        return ResultGenerator.failed(e.getCode(),e.getReason());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MinioException.class)
    public Result<?> minioException(MinioException e){
        log.error("minio操作异常: ",e);
        return ResultGenerator.failed(ResultCode.MINIO_EXCEPTION,e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKeyException(DuplicateKeyException e){
        log.error("数据库操作异常，唯一约束冲突: ",e);
        return ResultGenerator.failed(ResultCode.DATABASE_DUPLICATE_KEY_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public Result<?> sqlException(SQLException e){
        log.error("数据库操作异常: ",e);
        return ResultGenerator.failed(ResultCode.DATABASE_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public Result<?> dataAccessException(DataAccessException e){
        log.error("数据库操作异常: ",e);
        return ResultGenerator.failed(ResultCode.DATABASE_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(OAuth2ClientLoginException.class)
    public Result<?> oAuth2ClientLoginException(OAuth2ClientLoginException e){
        log.error("oauth2 client login error: ",e);
        return ResultGenerator.failed(ResultCode.OAUTH2_CLIENT_LOGIN_EXCEPTION,e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        log.error("未知异常: ",e);
        return ResultGenerator.failed(ResultCode.FAILED,e.getMessage());
    }


    //参数校验异常
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return getValidResult(e);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public Result<?> handlerBindException(BindException e){
        return getValidResult(e);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public Result<?> handlerValidationException(ValidationException e){
        return ResultGenerator.failed(ResultCode.PARAM_VALIDATION_EXCEPTION,e.getMessage());
    }

    private Result<?> getValidResult(Exception e){
        BindingResult bindingResult;
        if(e instanceof MethodArgumentNotValidException){
            bindingResult=((MethodArgumentNotValidException)e).getBindingResult();
        }else {
            bindingResult=((BindException)e).getBindingResult();
        }
        List<FieldError> fieldErrors=bindingResult.getFieldErrors();
        StringBuilder buffer=new StringBuilder();
        fieldErrors.forEach(f -> {
            buffer.append(f.getField()).append(":").append(f.getDefaultMessage()).append(";");
        });
        return ResultGenerator.failed(ResultCode.PARAM_VALIDATION_EXCEPTION,buffer.toString());
    }


}
