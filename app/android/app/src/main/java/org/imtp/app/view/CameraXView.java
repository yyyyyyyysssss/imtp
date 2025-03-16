package org.imtp.app.view;

import android.content.Context;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.imtp.app.R;

public class CameraXView extends FrameLayout {

    private PreviewView previewView;

    public CameraXView(Context context) {
        super(context);
        FrameLayout constraintLayout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.call, this,true);
        previewView = constraintLayout.findViewById(R.id.previewView);
//        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                for (int i = 0; i < getChildCount(); i++) {
//                    View child = getChildAt(i);
//                    child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
//                            MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
//                    child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
//                }
//                getViewTreeObserver().dispatchOnGlobalLayout();
//            }
//        });

        this.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                parent.measure(
                        MeasureSpec.makeMeasureSpec(getMeasuredWidth(),MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(getMeasuredHeight(),MeasureSpec.EXACTLY)
                );
                parent.layout(0,0,parent.getMeasuredWidth(),parent.getMeasuredHeight());
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });
    }

    public PreviewView getPreviewView() {
        return previewView;
    }


}
