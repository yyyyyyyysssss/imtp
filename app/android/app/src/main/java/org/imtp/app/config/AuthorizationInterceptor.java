package org.imtp.app.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.imtp.app.context.ClientContextHolder;
import org.imtp.app.context.DefaultClientContext;
import org.imtp.common.packet.body.TokenInfo;

import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/26 19:03
 */
public class AuthorizationInterceptor implements Interceptor {

    public AuthorizationInterceptor(){

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (ClientContextHolder.clientContext() != null){
            DefaultClientContext clientContext = (DefaultClientContext)ClientContextHolder.clientContext();
            TokenInfo tokenInfo = clientContext.tokenInfo();
            if (tokenInfo != null){
                Request request = original
                        .newBuilder()
                        .addHeader("Authorization", "Bearer "+ tokenInfo.getAccessToken())
                        .build();
                return chain.proceed(request);
            }
        }
        return chain.proceed(original);
    }
}
