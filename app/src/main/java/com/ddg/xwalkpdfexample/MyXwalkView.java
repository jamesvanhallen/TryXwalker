package com.ddg.xwalkpdfexample;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xwalk.core.XWalkView;

import java.io.IOException;

public class MyXwalkView extends XWalkView {

    public final static String TAG = "MyXWalkView";

    private ScrollOverListener mListener;

    private TouchEventListener mTouchEventListener;

    private FocusChangedListener mFocusChangedListener;

//    public MyXWalkView(Context context) {
//        super(context);
//    }

    public MyXwalkView(Context context, Activity activity) {
        super(context, activity);
    }

    public MyXwalkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "Dispath");
        mTouchEventListener.onTouchEventInvoked(ev);
        return super.dispatchTouchEvent(ev);
    }



    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if(null != mListener){
            Log.i(TAG, "onOverScrolled is invoked. Parameter: scrollX=" + scrollX + "; scrollY=" + scrollY + "; clampedX=" + clampedX + "; clampedY=" + clampedY);
            mListener.onScrollOver("onOverScrolled is invoked. Parameter: scrollX=" + scrollX + "; scrollY=" + scrollY + "; clampedX=" + clampedX + "; clampedY=" + clampedY);
        }
    }

    public void setScrollOverListener(ScrollOverListener listener){
        this.mListener = listener;
    }

    public interface ScrollOverListener{

        public void onScrollOver(String msg);

    }

    public void setTouchEventListener(TouchEventListener listener){
        this.mTouchEventListener = listener;
    }

    public interface TouchEventListener{

        public void onTouchEventInvoked(MotionEvent ev);

    }

    public void setFocuseChangedListener(FocusChangedListener listener){
        this.mFocusChangedListener = listener;
    }

    public interface FocusChangedListener{

        public void informFocuseChanged(String msg);

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Log.i(TAG, "onFocusChanged is invoked, focused:"
                + focused + "; direction:" + direction
                + ": previouslyFocusedRect: " + previouslyFocusedRect);
        if(null != mFocusChangedListener) {
            mFocusChangedListener.informFocuseChanged("onFocusChanged is invoked, focused:"
                    + focused + "; direction:" + direction
                    + ": previouslyFocusedRect: " + previouslyFocusedRect);
        }
    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onTouchEvent is invoked");
//        float initialX, initialY;
//        int action = event.getActionMasked();
//        initialX = event.getX();
//        initialY = event.getY();
//        if(null != mTouchEventListener) {
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    Log.i(TAG, "onTouchEvent is invoked; Action was DOWN");
//                    this.mTouchEventListener.onTouchEventInvoked("onTouchEvent is invoked; Action was DOWN");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i(TAG, "onTouchEvent is invoked; Action was MOVE");
//                    this.mTouchEventListener.onTouchEventInvoked("onTouchEvent is invoked; Action was MOVE");
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.i(TAG, "onTouchEvent is invoked; Action was UP");
//                    this.mTouchEventListener.onTouchEventInvoked("onTouchEvent is invoked; Action was UP");
//                    break;
//
//                case MotionEvent.ACTION_CANCEL:
//                    Log.i(TAG, "onTouchEvent is invoked; Action was CANCEL");
//                    this.mTouchEventListener.onTouchEventInvoked("onTouchEvent is invoked; Action was CANCEL");
//                    break;
//
//                case MotionEvent.ACTION_OUTSIDE:
//                    Log.i(TAG, "onTouchEvent is invoked; Movement occurred outside bounds of current screen element");
//                    this.mTouchEventListener.onTouchEventInvoked("onTouchEvent is invoked; Movement occurred outside bounds of current screen element");
//                    break;
//            }
//        }
//        return super.onTouchEvent(event);
//    }
}