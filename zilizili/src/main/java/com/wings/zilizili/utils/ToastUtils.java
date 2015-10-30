package com.wings.zilizili.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wing on 2015/8/16.
 * 单例吐司工具
 */
public class ToastUtils {
    private static Toast mToast;
    //单例吐司,直接覆盖前一个,不再挨个执行
    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
