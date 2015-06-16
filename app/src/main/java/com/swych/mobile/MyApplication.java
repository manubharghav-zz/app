package com.swych.mobile;

import android.app.Application;

import com.swych.mobile.networking.RequestManager;

/**
 * Created by manu on 6/15/15.
 */
public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.getInstance(getApplicationContext());
    }
}
