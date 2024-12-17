package org.imtp.app.module;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class UploadChunkModule extends ReactContextBaseJavaModule {

    @NonNull
    @Override
    public String getName() {
        return "UploadChunkModule";
    }

    @ReactMethod
    public void upload(String filePath, Promise promise){
        System.out.println("filePath: " + filePath);
        promise.resolve(filePath);
    }

}
