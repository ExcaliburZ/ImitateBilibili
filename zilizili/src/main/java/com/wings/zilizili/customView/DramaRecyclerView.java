package com.wings.zilizili.customView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wing on 2015/10/25.
 * 申请父控件不要拦截自己的触碰事件的RecyclerView
 */
public class DramaRecyclerView extends RecyclerView {
    public DramaRecyclerView(Context context) {
        super(context);
    }

    public DramaRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DramaRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //申请父控件不要拦截自己的触碰事件
        getParent().requestDisallowInterceptTouchEvent(true);
        System.out.println("onInterceptTouchEvent");
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        System.out.println("DramaRecyclerView onTouchEvent");
        return super.onTouchEvent(e);
    }
}
