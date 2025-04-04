package org.imtp.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/29 13:41
 */
public class JsonUtil {


    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(long.class,ToStringSerializer.instance);
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    public static String toJSONString(Object object){
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String s,Class<T> tClass){
        try {
            return OBJECT_MAPPER.readValue(s,tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(byte[] bytes,Class<T> tClass){
        try {
            return OBJECT_MAPPER.readValue(bytes,tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String s, TypeReference<T> typeReference){
        try {
            return OBJECT_MAPPER.readValue(s,typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String s,Class<T> tClass){
        CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, tClass);
        try {
            return OBJECT_MAPPER.readValue(s,collectionType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(byte[] bytes,Class<T> tClass){
        CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, tClass);
        try {
            return OBJECT_MAPPER.readValue(bytes,collectionType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
