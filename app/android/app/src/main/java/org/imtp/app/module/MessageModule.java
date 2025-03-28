package org.imtp.app.module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.imtp.app.ConnectListener;
import org.imtp.app.NettyClient;
import org.imtp.app.context.ClientContext;
import org.imtp.app.context.ClientContextHolder;
import org.imtp.app.model.MessageListenerListener;
import org.imtp.app.model.MessageModel;
import org.imtp.app.model.Observer;
import org.imtp.common.packet.AuthenticationRequest;
import org.imtp.common.packet.HeartbeatPingMessage;
import org.imtp.common.packet.HeartbeatPongMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.utils.JsonUtil;

public class MessageModule extends ReactContextBaseJavaModule implements Observer {

    private static final String TAG = "MessageModule";

    private static NettyClient nettyClient;

    private static final MessageModel messageModel = new MessageModel();

    private final ReactApplicationContext reactApplicationContext;

    public MessageModule(ReactApplicationContext reactApplicationContext){
        this.reactApplicationContext = reactApplicationContext;
        messageModel.removeObserver(MessageModule.class);
        messageModel.registerObserver(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "MessageModule";
    }


    @ReactMethod
    public void init(String tokenInfoJson, Promise promise){
        if (nettyClient != null){
            promise.resolve(true);
            return;
        }
        TokenInfo tokenInfo = JsonUtil.parseObject(tokenInfoJson, TokenInfo.class);
        nettyClient = new NettyClient(tokenInfo, messageModel);
        nettyClient.addListener(new ConnectListener() {
            @Override
            public void connected() {
                messageModel.sendMessage(new AuthenticationRequest(tokenInfo.getAccessToken()));
                promise.resolve(true);
            }

            @Override
            public void exception(Throwable throwable) {
                promise.reject(throwable);
            }
        });
        //启动客户端
        new Thread(nettyClient).start();
    }

    @ReactMethod
    public void destroy(Promise promise){
        Log.i(TAG,"MessageModule destroy");
        if (nettyClient != null){
            nettyClient.stop();
            nettyClient = null;
        }
        promise.resolve(true);
    }

    @ReactMethod
    public void addListener(String eventName) {
        Log.i(TAG,"addListener eventName: " + eventName);
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        Log.i(TAG,"removeListeners count: " + count);
    }

    @ReactMethod
    public void sendMessage(String messageJson,Promise promise){
        if (messageJson == null || messageJson.isEmpty()){
            promise.reject(new NullPointerException("message not null"));
            return;
        }
        ReactNativeMessage reactNativeMessage = JsonUtil.parseObject(messageJson, ReactNativeMessage.class);
        messageModel.sendMessage(new ReactNativeAdapterMessage(reactNativeMessage), new MessageListenerListener() {
            @Override
            public void succeed() {
                promise.resolve(true);
            }

            @Override
            public void exception(Throwable throwable) {
                promise.reject(throwable);
            }
        });

    }
    public void receiveMessage(Packet packet){
        DeviceEventManagerModule.RCTDeviceEventEmitter rctDeviceEventEmitter = reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        Log.i(TAG,JsonUtil.toJSONString(packet));
        rctDeviceEventEmitter.emit("RECEIVE_MESSAGE",JsonUtil.toJSONString(packet));
    }

    @Override
    public void update(Object object) {
        if (object instanceof Packet packet){
            receiveMessage(packet);
        }
    }
}
