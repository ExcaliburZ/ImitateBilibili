package com.wings.zilizili.customView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wing on 2015/10/24.
 * 不拦截子控件TouchEvent的SwipeRefreshLayout
 */

public class LowPrioritySwipeRefreshLayout extends SwipeRefreshLayout {
    public LowPrioritySwipeRefreshLayout(Context context) {
        super(context);
    }

    public LowPrioritySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        return false;
//        return super.onInterceptTouchEvent(event);
    }

}
