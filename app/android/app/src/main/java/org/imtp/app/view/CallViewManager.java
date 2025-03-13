package org.imtp.app.view;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import org.webrtc.SurfaceViewRenderer;

public class CallViewManager extends SimpleViewManager<ConstraintLayout> {

    private static final String TAG = "CallViewManager";

    private ReactApplicationContext reactApplicationContext;

    public CallViewManager(ReactApplicationContext reactApplicationContext){
        this.reactApplicationContext = reactApplicationContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "CallView";
    }

    @Override
    public void receiveCommand(@NonNull ConstraintLayout root, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);

    }

    public void test(){
        Log.i(TAG,"CallViewManager Test");
    }

    @NonNull
    @Override
    protected ConstraintLayout createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        ConstraintLayout constraintLayout = new ConstraintLayout(themedReactContext);
        constraintLayout.setId(View.generateViewId());
        TextView textView = new TextView(themedReactContext);
        textView.setText("hello word");
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);

        constraintLayout.addView(textView);

        return constraintLayout;
    }
}
