package com.haosu.schedulebook;


import android.app.Application;
import android.util.Log;

import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;
import com.xiaomi.mistatistic.sdk.controller.HttpEventFilter;
import com.xiaomi.mistatistic.sdk.data.HttpEvent;

import org.xutils.x;


public class MyApplication extends Application {

    private String miAppID = "2882303761517466171";
    private String miAppKey = "5951746678171";


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能

        // regular stats.
        MiStatInterface.initialize(this.getApplicationContext(), miAppID, miAppKey,
                "default channel");
        MiStatInterface.setUploadPolicy(
                MiStatInterface.UPLOAD_POLICY_WHILE_INITIALIZE, 0);
        MiStatInterface.enableLog();
        Log.i("MI_STAT", MiStatInterface.getDeviceID(this) + " is the device.");

        // enable exception catcher.
        MiStatInterface.enableExceptionCatcher(true);

        // enable network monitor
        URLStatsRecorder.enableAutoRecord();
        URLStatsRecorder.setEventFilter(new HttpEventFilter() {

            @Override
            public HttpEvent onEvent(HttpEvent event) {
                Log.d("MI_STAT", event.getUrl() + " result =" + event.toJSON());
                // returns null if you want to drop this event.
                // you can modify it here too.
                return event;
            }
        });

    }
}