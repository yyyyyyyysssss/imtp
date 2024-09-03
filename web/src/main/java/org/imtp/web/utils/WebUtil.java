package org.imtp.web.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class WebUtil {

    public static Map<String,String> requestParamConvertMap(String requestParam){
        if (StringUtils.isEmpty(requestParam)){
            return new HashMap<>();
        }
        Map<String,String> map = new HashMap<>();
        String[] params = requestParam.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

}
