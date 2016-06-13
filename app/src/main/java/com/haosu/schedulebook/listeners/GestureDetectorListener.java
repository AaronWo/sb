package com.haosu.schedulebook.listeners;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by haosu on 2016/6/13.
 */
public class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

    private Activity activity;

    public GestureDetectorListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(this.getClass().getSimpleName(), "onFling " + e1 + " " + e2 + " " + velocityX);
        if (velocityX > 400) {
            activity.finish();
        }
        return true;
    }
}
