package org.imtp.client.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/22 14:11
 */
public class ClientContext {


    private static final Map<String,String> CONTEXT_MAP = new ConcurrentHashMap<>();

    private static final String CURRENT_USER = "user_key";


    public static void setUser(String user){
        CONTEXT_MAP.put(CURRENT_USER,user);
    }

    public static String getUser(){
        return CONTEXT_MAP.get(CURRENT_USER);
    }

}
