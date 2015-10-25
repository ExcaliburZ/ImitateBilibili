package com.wings.zilizili.utils;

/**
 * Created by wing on 2015/10/25.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.lidroid.xutils.task.TaskHandler;

public class MyPauseOnScrollListener extends RecyclerView.OnScrollListener {
    private TaskHandler taskHandler;
    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final AbsListView.OnScrollListener externalListener;

    public MyPauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling) {
        this(taskHandler, pauseOnScroll, pauseOnFling, (AbsListView.OnScrollListener) null);
    }

    public MyPauseOnScrollListener(TaskHandler taskHandler, boolean pauseOnScroll, boolean pauseOnFling, AbsListView.OnScrollListener customListener) {
        this.taskHandler = taskHandler;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.externalListener = customListener;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case 0:
                this.taskHandler.resume();
                break;
            case 1:
                if (this.pauseOnScroll) {
                    this.taskHandler.pause();
                }
                break;
            case 2:
                if (this.pauseOnFling) {
                    this.taskHandler.pause();
                }
        }

        if (this.externalListener != null) {
            this.externalListener.onScrollStateChanged(view, scrollState);
        }

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.externalListener != null) {
            this.externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }
}
