package org.imtp.app.module;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.imtp.app.activity.CallActivity;
import org.imtp.app.enums.CallType;

import java.util.Objects;

public class CallModule extends ReactContextBaseJavaModule {

    private static final String TAG = "CallModule";

    private ReactApplicationContext reactApplicationContext;
    public CallModule(ReactApplicationContext reactApplicationContext){
        super(reactApplicationContext);
        this.reactApplicationContext = reactApplicationContext;
    }

    @Override
    public String getName() {
        return "CallModule";
    }

    @ReactMethod
    public void call(String callType){
        Log.i(TAG,"callType: " + callType);
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null){
            Intent intent = new Intent(getCurrentActivity(), CallActivity.class);
            currentActivity.startActivity(intent);
        }else {
            Log.e(TAG,"currentActivity is null");
        }
    }

}
