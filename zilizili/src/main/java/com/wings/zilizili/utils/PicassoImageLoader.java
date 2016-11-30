package com.wings.zilizili.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wings.zilizili.R;

/**
 * 单例的Picasso图片加载器
 */
public enum PicassoImageLoader {
    INSTANCE;
    String ANDROID_RESOURCE = "android.resource://";
    String FOREWARD_SLASH = "/";

    public void displayRecommendImage(Context context, String uri, ImageView view) {
        Picasso
                .with(context)
                .load(uri)
                .placeholder(R.drawable.bili_default_image_tv_16_10)
                .into(view);
    }

    public void displayBangumiImage(Context context, String uri, ImageView view) {
        Picasso
                .with(context)
                .load(uri)
                .placeholder(R.drawable.bili_default_image_tv_12_16)
                .into(view);
    }

    public void displayTopNewsImage(Context context, String uri, ImageView view) {
        Picasso
                .with(context)
                .load(uri)
                .placeholder(R.drawable.bili_default_image_tv_32_10)
                .into(view);
    }

}
