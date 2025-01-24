package org.imtp.server.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/10 14:57
 */
public class WebApiRequestInterceptor implements RequestInterceptor {


    @Value("${im.web.apikey}")
    private String apikey;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("apikey",apikey);
    }
}
