package org.imtp.app;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import org.imtp.app.module.CallModule;
import org.imtp.app.module.MessageModule;
import org.imtp.app.module.UploadModule;
import org.imtp.app.view.CallViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomNativeModulePackage implements ReactPackage {
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactApplicationContext) {
        List<NativeModule> nativeModules = new ArrayList<>();
        //初始化netty客户端
        nativeModules.add(new MessageModule(reactApplicationContext));
        //注册文件上传模块
        nativeModules.add(new UploadModule(reactApplicationContext));
        //音视频通话
        nativeModules.add(new CallModule(reactApplicationContext));
        return nativeModules;
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactApplicationContext) {
        List<ViewManager> viewManagers = new ArrayList<>();
        CallViewManager callViewManager = new CallViewManager(reactApplicationContext);
        viewManagers.add(callViewManager);
        return viewManagers;
    }
}
