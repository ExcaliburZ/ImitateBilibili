package com.wings.zilizili.global;

import android.app.Application;
import android.content.Context;

import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

/**
 * Created by wing on 2015/10/27.
 * 应用的主Application类,负责初始化一些数据
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesMgr.init(this, "config");
        mContext = getApplicationContext();
    }

    /**
     * 提供一个全局的上下文
     */
    public static Context getContext() {
        return mContext;
    }
}
