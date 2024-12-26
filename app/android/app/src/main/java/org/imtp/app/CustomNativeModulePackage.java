package org.imtp.app;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import org.imtp.app.module.NettyClientModule;
import org.imtp.app.module.UploadModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomNativeModulePackage implements ReactPackage {
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactApplicationContext) {
        List<NativeModule> nativeModules = new ArrayList<>();
        //初始化netty客户端
        nativeModules.add(new NettyClientModule());
        //注册文件上传模块
        nativeModules.add(new UploadModule(reactApplicationContext));
        return nativeModules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }
}
