package com.wings.zilizili.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.wings.zilizili.R;

import derson.com.multipletheme.colorUi.widget.ColorToolbar;

public class VideoDetailActivity extends BaseActivity {

    private ColorToolbar mColorToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        mColorToolbar = $(R.id.toolbar);
        setSupportActionBar(mColorToolbar);
        //设置取消默认标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_detail, menu);
        return true;
    }

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

}
