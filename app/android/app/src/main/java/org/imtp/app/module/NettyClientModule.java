package org.imtp.app.module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.imtp.app.context.ClientContextHolder;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.utils.JsonUtil;

public class NettyClientModule extends ReactContextBaseJavaModule {

    private static final String TAG = "NettyClientModule";

    @NonNull
    @Override
    public String getName() {
        return "NettyClientModule";
    }


    @ReactMethod
    public void init(String tokenInfoJson, Promise promise){
        Log.i(TAG,"NettyClient init");
        TokenInfo tokenInfo = JsonUtil.parseObject(tokenInfoJson, TokenInfo.class);
        //初始化上下文对象
        ClientContextHolder.createClientContext(null,tokenInfo);
        promise.resolve(true);
    }

    public void destroy(Promise promise){
        Log.i(TAG,"NettyClient destroy");
        promise.resolve(true);
    }

}
