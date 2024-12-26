package org.imtp.app.module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.imtp.common.utils.JsonUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class UploadModule extends ReactContextBaseJavaModule {

    private static final String TAG = "UploadModule";

    private ReactApplicationContext reactApplicationContext;

    public UploadModule(ReactApplicationContext reactApplicationContext){
        this.reactApplicationContext = reactApplicationContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "UploadModule";
    }

    @ReactMethod
    public void uploadId(String fileInfoJson,Promise promise){
        FileInfo fileInfo = JsonUtil.parseObject(fileInfoJson,FileInfo.class);
        ChunkedUploader.uploadId(fileInfo).whenComplete((result,ex) -> {
            if (ex != null){
                promise.reject(ex);
            }else {
                promise.resolve(result);
            }
        });
    }

    @ReactMethod
    public void upload(String fileInfoJson, Promise promise){
        FileInfo fileInfo = JsonUtil.parseObject(fileInfoJson,FileInfo.class);
        Long fileSize = fileInfo.getFileSize();
        String uploadId = fileInfo.getUploadId();
        AtomicLong count = new AtomicLong(0);
        CompletableFuture<String> completableFuture = ChunkedUploader.uploadFile(fileInfo, new ProgressListener() {
            @Override
            public void onProgress(long bytesWritten) {
                long uploadedSize = count.addAndGet(bytesWritten);
                uploadProgressEvent(uploadId,uploadedSize);
                Log.i(TAG,"onProgress totalSize:" + fileSize + " uploadedSize:" + uploadedSize + " progress: " + (double)uploadedSize / fileSize * 100);
            }
        });
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

    //发送上传进度事件
    private void uploadProgressEvent(String uploadId,Long uploadedSize){
        DeviceEventManagerModule.RCTDeviceEventEmitter rctDeviceEventEmitter = reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        rctDeviceEventEmitter.emit(uploadId,uploadedSize.toString());
    }

}
