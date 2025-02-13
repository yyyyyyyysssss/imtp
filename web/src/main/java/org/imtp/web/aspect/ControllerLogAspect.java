package org.imtp.web.aspect;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2025/2/12 17:08
 */
@Aspect
@Slf4j
@Component
public class ControllerLogAspect {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(MultipartFile.class, new MultipartFileSerializer());
        simpleModule.addSerializer(HttpServletRequest.class, new HttpServletRequestSerializer());
        simpleModule.addSerializer(HttpServletResponse.class, new HttpServletResponseSerializer());
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    //切点
    @Pointcut("execution(* org.imtp.web.controller..*.*(..))")
    public void controllers() {
    }

    @Around("controllers()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            stringBuilder.append(String.format("Request: [%s] %s", request.getMethod(), request.getRequestURI()));
        }
        stringBuilder.append(String.format("   ,Args: %s",  OBJECT_MAPPER.writeValueAsString(joinPoint.getArgs())));
        long s = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            long diff = System.currentTimeMillis() - s;
            stringBuilder.append(String.format("   Spend: %f s", diff / 1000.0));
            stringBuilder.append(String.format("   ,Exception: %s", e));
            log.error(stringBuilder.toString());
            throw e;
        }
        long e = System.currentTimeMillis();
        long diff = e - s;
        stringBuilder.append(String.format("   Spend: %f s", diff / 1000.0));
        stringBuilder.append(String.format("   ,Return: %s", OBJECT_MAPPER.writeValueAsString(result)));
        log.info(stringBuilder.toString());

        return result;
    }


    static class MultipartFileSerializer extends JsonSerializer<MultipartFile>{

        @Override
        public void serialize(MultipartFile file, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // 忽略 MultipartFile 对象的序列化
            jsonGenerator.writeString(file.getOriginalFilename());
        }
    }

    static class HttpServletRequestSerializer extends JsonSerializer<HttpServletRequest>{

        @Override
        public void serialize(HttpServletRequest request, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // 忽略 HttpServletRequest 对象的序列化
            jsonGenerator.writeNull();
        }
    }

    static class HttpServletResponseSerializer extends JsonSerializer<HttpServletResponse>{

        @Override
        public void serialize(HttpServletResponse response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // 忽略 HttpServletResponse 对象的序列化
            jsonGenerator.writeNull();
        }
    }

}
