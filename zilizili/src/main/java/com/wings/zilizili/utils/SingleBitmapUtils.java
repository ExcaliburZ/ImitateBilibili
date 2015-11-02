package com.wings.zilizili.utils;

import com.lidroid.xutils.BitmapUtils;
import com.wings.zilizili.global.MyApplication;

/**
 * Created by wing on 2015/11/2.
 */
public class SingleBitmapUtils {
    private static SingleBitmapUtils instance = new SingleBitmapUtils();
    private BitmapUtils mBitmapUtils;

    private SingleBitmapUtils() {
        mBitmapUtils = new BitmapUtils(MyApplication.getContext());
//        mBitmapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);
//        mBitmapUtils.clearDiskCache();
//        mBitmapUtils.clearCache();
//        mBitmapUtils.clearMemoryCache();
        System.out.println("Clear !!!!!!!!!!");
    }

    public static SingleBitmapUtils getInstance() {
        return instance;
    }

    public BitmapUtils getBitmapUtils() {
        return mBitmapUtils;
    }
}
