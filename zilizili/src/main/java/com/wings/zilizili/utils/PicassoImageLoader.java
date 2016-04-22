package com.wings.zilizili.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wings.zilizili.R;

/**
 * Created by wing on 2016/4/22.
 */
public class PicassoImageLoader {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";
    private static PicassoImageLoader instance = new PicassoImageLoader();

    private PicassoImageLoader() {
    }

    public static PicassoImageLoader getInstance() {
        return instance;
    }

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
