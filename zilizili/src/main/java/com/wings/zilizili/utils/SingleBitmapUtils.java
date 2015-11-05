package com.wings.zilizili.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;
import com.wings.zilizili.global.MyApplication;

import java.io.File;

/**
 * Created by wing on 2015/11/2.
 */
public class SingleBitmapUtils {
    private static SingleBitmapUtils instance = new SingleBitmapUtils();
    private BitmapUtils mBitmapUtils;

    private SingleBitmapUtils() {
        Context context = MyApplication.getContext();
        String cachePath = context.getCacheDir().getPath() + File.separator + "ImageCache";
        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdir();
        }
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getPath());
        mBitmapUtils = new BitmapUtils(context, file.getAbsolutePath());
//        mBitmapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);
//        mBitmapUtils.clearDiskCache();
//        mBitmapUtils.clearCache();
//        mBitmapUtils.clearMemoryCache();
    }

    public static SingleBitmapUtils getInstance() {
        return instance;
    }

    public BitmapUtils getBitmapUtils() {
        return mBitmapUtils;
    }
}
