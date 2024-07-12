package org.imtp.client.component;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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
@Slf4j
public class OKHttpClientHelper {

    private final OkHttpClient okHttpClient;

    private static volatile OKHttpClientHelper okHttpClientHelper;

    private static final Lock lock = new ReentrantLock();

    private OKHttpClientHelper() {
        this.okHttpClient = okHttpClient();
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


    public <T> T doGet(String url, Class<T> tClass) {
        return doGet(url, new TypeReference<T>() {});
    }

    public <T> T doGet(String url, TypeReference<T> typeReference) {
        Request request = new Request
                .Builder()
                .get()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body;
                if ((body = response.body()) != null) {
                    String str = body.string();
                    return JsonUtil.parseObject(str, typeReference);
                }
                return null;
            }else {
                throw new RuntimeException("Request failed : " + url);
            }
        } catch (IOException e) {
            log.error("Request Error", e);
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
                .build();

    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, new TrustManager[]{new TrustAllCerts()}, null);
            sslSocketFactory = tls.getSocketFactory();
        } catch (Exception e) {
            log.error("init okhttp SSLSocketFactory error", e);
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
