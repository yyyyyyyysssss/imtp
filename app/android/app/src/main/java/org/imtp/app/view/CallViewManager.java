package org.imtp.app.view;

import android.util.Log;
import android.view.Choreographer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CallViewManager extends SimpleViewManager<ConstraintLayout> {

    private static final String TAG = "CallViewManager";

    private final static String SIGNALING_PRE_OFFER = "SIGNALING_PRE_OFFER";

    private final static String SIGNALING_OFFER = "SIGNALING_OFFER";
    private final static String SIGNALING_ANSWER = "SIGNALING_ANSWER";
    private final static String SIGNALING_CANDIDATE = "SIGNALING_CANDIDATE";
    private final static String SIGNALING_BUSY = "SIGNALING_BUSY";
    private final static String SIGNALING_CLOSE = "SIGNALING_CLOSE";

    private final static String START_CAMERA = "START_CAMERA";

    //切换摄像头
    private final static String SWITCH_CAMERA = "SWITCH_CAMERA";

    private ReactApplicationContext reactApplicationContext;

    private ThemedReactContext themedReactContext;

    private PreviewView previewView;

    private Preview preview;

    private ProcessCameraProvider processCameraProvider;

    public CallViewManager(ReactApplicationContext reactApplicationContext) {
        this.reactApplicationContext = reactApplicationContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "CallView";
    }


    @Override
    public void receiveCommand(@NonNull ConstraintLayout root, String commandId, @Nullable ReadableArray args) {
        String p = args.getString(0);
        switch (commandId) {
            case SIGNALING_PRE_OFFER:
                Log.i(TAG, p);
                break;
            case SIGNALING_OFFER:
                Log.i(TAG, p);
                break;
            case SIGNALING_ANSWER:
                Log.i(TAG, p);
                break;
            case SIGNALING_CANDIDATE:
                Log.i(TAG, p);
                break;
            case SIGNALING_BUSY:
                Log.i(TAG, p);
                break;
            case SIGNALING_CLOSE:
                Log.i(TAG, p);
                break;
            case START_CAMERA:
                String defaultLensFacing = args.getString(0);
                startCamera(Integer.parseInt(defaultLensFacing));
                break;
            case SWITCH_CAMERA:
                String lensFacing = args.getString(0);
                Log.i(TAG, "switch camera: " + lensFacing);
                try {
                    this.bindCamera(Integer.parseInt(lensFacing));
                } catch (CameraInfoUnavailableException e) {
                    Log.e(TAG, "switch camera failed", e);
                }
                break;
        }
    }

    @NonNull
    @Override
    protected ConstraintLayout createViewInstance(@NonNull ThemedReactContext themedReactContext) {
        this.themedReactContext = themedReactContext;
        this.previewView = new PreviewView(themedReactContext);
        previewView.setId(View.generateViewId());

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );

        ConstraintLayout constraintLayout = new ConstraintLayout(themedReactContext);
        constraintLayout.setId(View.generateViewId());
        constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));

        constraintLayout.addView(previewView, layoutParams);

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                for (int i = 0; i < constraintLayout.getChildCount(); i++) {
                    View child = constraintLayout.getChildAt(i);
                    child.measure(View.MeasureSpec.makeMeasureSpec(constraintLayout.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(constraintLayout.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
                    child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                }
                constraintLayout.getViewTreeObserver().dispatchOnGlobalLayout();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });


        return constraintLayout;
    }

    public void startCamera(int lensFacing) {
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this.themedReactContext);
        listenableFuture.addListener(() -> {
            try {
                this.processCameraProvider = listenableFuture.get();

                this.preview = new Preview.Builder().build();
                this.preview.setSurfaceProvider(previewView.getSurfaceProvider());

                bindCamera(lensFacing);
            } catch (ExecutionException | InterruptedException | CameraInfoUnavailableException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this.themedReactContext));
    }

    public void bindCamera(int lensFacing) throws CameraInfoUnavailableException {
        //前置or后置摄像头
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        LifecycleOwner lifecycleOwner = (LifecycleOwner) themedReactContext.getCurrentActivity();
        if (lifecycleOwner != null) {
            //解绑所有用例
            this.processCameraProvider.unbindAll();
            //重新绑定用例
            this.processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview);
        } else {
            Log.e(TAG, "LifecycleOwner is null. Cannot bind camera");
        }
    }
}
