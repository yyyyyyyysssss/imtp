package org.imtp.app.config;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.*;

import org.imtp.app.AppConfig;
import org.imtp.common.utils.JsonUtil;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/12 14:59
 */
public class OKHttpClientHelper {

    private static final String TAG = "OKHttpClientHelper";

    private AppConfig appConfig;

    private final OkHttpClient okHttpClient;

    private static volatile OKHttpClientHelper okHttpClientHelper;

    private static final Lock lock = new ReentrantLock();

    private OKHttpClientHelper() {
        this.okHttpClient = okHttpClient();
        this.appConfig = AppConfig.getInstance();
    }

    public static OKHttpClientHelper getInstance() {
        if (okHttpClientHelper == null) {
            try {
                if (okHttpClientHelper == null) {
                    lock.lock();
                    okHttpClientHelper = new OKHttpClientHelper();
                }
            } finally {
                lock.unlock();
            }
        }
        return okHttpClientHelper;
    }

    public <T> T doGet(String url, TypeReference<T> typeReference) {
        Request request = new Request
                .Builder()
                .url(appConfig.getApiHost() + url)
                .get()
                .build();
        return execute(request,typeReference);
    }

    public <T> T doPost(String url,Object body,TypeReference<T> typeReference){
        RequestBody requestBody = RequestBody.create(JsonUtil.toJSONString(body), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request
                .Builder()
                .url(appConfig.getApiHost() + url)
                .post(requestBody)
                .build();
        return execute(request,typeReference);
    }

    public <T> T doPost(String url,RequestBody requestBody,TypeReference<T> typeReference){
        Request request = new Request
                .Builder()
                .url(appConfig.getApiHost() + url)
                .post(requestBody)
                .build();
        return execute(request,typeReference);
    }

    public void doPost(String url,Object body,Callback callback){
        RequestBody requestBody = RequestBody.create(JsonUtil.toJSONString(body), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request
                .Builder()
                .url(appConfig.getApiHost() + url)
                .post(requestBody)
                .build();
        execute(request,callback);
    }

    public void execute(Request request,Callback callback){
        okHttpClient.newCall(request).enqueue(callback);
    }

    public <T> T execute(Request request,TypeReference<T> typeReference){
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
                String str;
                if (body != null && !(str = body.string()).isEmpty()) {
                    return JsonUtil.parseObject(str, typeReference);
                }
                return null;
            }else {
                String error = null;
                if (body != null) {
                    error = body.string();
                }
                throw new RuntimeException("Request Failed Url: " + request.url().url().getPath() + "; response code : " + response.code() + "; error msg : " + error);
            }
        } catch (IOException e) {
            Log.e(TAG, "Request Error " + e.getMessage());
        }
        return null;
    }


    private OkHttpClient okHttpClient() {
        return new OkHttpClient
                .Builder()
                .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                .hostnameVerifier((h, s) -> true)
                .addInterceptor(new AuthorizationInterceptor())
                .build();

    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, new TrustManager[]{new TrustAllCerts()}, null);
            sslSocketFactory = tls.getSocketFactory();
        } catch (Exception e) {
            Log.e(TAG, "init okhttp SSLSocketFactory error " + e.getMessage());
        }
        return sslSocketFactory;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

}
