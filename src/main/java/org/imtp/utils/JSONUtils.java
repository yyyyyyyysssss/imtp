package org.imtp.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/4 10:46
 */
public class JSONUtils {


    public static <T> T bytesTransformObject(byte[] bytes,Class<T> t){
        return JSONObject.parseObject(new String(bytes, StandardCharsets.UTF_8),t);
    }

    public static <T> byte[] objectTransformBytes(T t){
        String jsonStr = JSON.toJSONString(t);
        return jsonStr.getBytes(StandardCharsets.UTF_8);
    }

}
