package com.wings.zilizili.global;

import android.app.Application;

import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

/**
 * Created by wing on 2015/10/27.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesMgr.init(this, "config");
    }
}
