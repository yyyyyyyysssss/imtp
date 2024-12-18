package org.imtp.app.module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.imtp.common.utils.JsonUtil;

import java.util.concurrent.CompletableFuture;

public class UploadModule extends ReactContextBaseJavaModule {

    private static final String TAG = "UploadModule";

    @NonNull
    @Override
    public String getName() {
        return "UploadModule";
    }

    @ReactMethod
    public void upload(String fileInfoJson, Promise promise){
        FileInfo fileInfo = JsonUtil.parseObject(fileInfoJson,FileInfo.class);
        CompletableFuture<String> completableFuture = ChunkedUploader.uploadFile(fileInfo);
        completableFuture.whenComplete((r,e) -> {
            if (e != null) {
                Log.e(TAG,"upload chunk failed: ", e);
                promise.reject(e);
            }
        }).thenAccept(r -> {
            Log.i(TAG,"upload completed accessUrl: " + r);
            promise.resolve(r);
        });
    }

}
