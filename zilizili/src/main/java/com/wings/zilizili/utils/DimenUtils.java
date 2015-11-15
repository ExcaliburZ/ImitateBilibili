package com.wings.zilizili.utils;

import android.content.Context;

/**
 * Created by wing on 2015/8/7.
 */
public class DimenUtils {
    public static float dp2Pix(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (float) (dp * density + 0.5);
    }
}
