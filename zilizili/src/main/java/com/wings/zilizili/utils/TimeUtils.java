package com.wings.zilizili.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wing on 2015/9/1.
 */
public class TimeUtils {
    public static String LongToStr(Long time) {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式.
        int sec = (int) (time / 1000);
        String minute = sec / 60 == 0 ? "00" : String.valueOf(sec / 60);
        int second = sec % 60;
        return minute + ":" + (second >= 10 ? second : "0" + second);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");//初始化Formatter的转换格式.
        return formatter.format(new Date());
    }
}
