package com.wings.zilizili.customView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wing on 2015/7/23.
 */
public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //True if the event was handled, false otherwise.
        System.out.println("onTouchEvent");
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("onInterceptHoverEvent");
        return false;
    }
}
