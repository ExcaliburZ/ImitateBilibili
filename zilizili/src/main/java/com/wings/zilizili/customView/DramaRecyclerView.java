package com.wings.zilizili.customView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wing on 2015/10/25.
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
    public boolean onInterceptHoverEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptHoverEvent(event);
    }
}
